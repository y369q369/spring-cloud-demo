# 微服务框架

[toc]

## 引言

### 参考

> 1、 视频：https://www.bilibili.com/video/BV19C4y187Ar
>
> 2、 书籍：《SpringCloud微服务实战.pdf》



## 一、SpringCloud介绍

### 1.1 微服务架构

> https://martinfowler.com/articles/microservices.html
>
> 微服务架构提出者:马丁福勒

> 简而言之，微服务体系结构[样式 [1\]](https://martinfowler.com/articles/microservices.html#footnote-etymology)是一种将单个应用程序开发为一组小型服务的方法，每个应用程序在自己的进程中运行，并与轻量级机制（通常是 HTTP 资源 API）通信。这些服务围绕业务功能构建，可通过全自动部署机制独立部署。这些服务的集中管理最少，可能以不同的编程语言编写，并使用不同的数据存储技术。

### 1.2 SpringCloud介绍

> SpringCloud是微服务架构落地的一套技术栈
>
> SpringCloud中的大多数技术都是基于Netflix公司的技术进行第二次开发。
>
> 1、SpringCloud的中文社区网站：http://springcloud.cn/
>
> 2、SpringCloud的中文网 ：http://springcloud.cc/
>
> 八个技术点：
>
> 1、Eureka - 服务的注册与发现
>
> 2、Robbn - 服务之间的负载均衡
>
> 3、Feign - 服务之间的通讯
>
> 4、Hystrix - 服务的线程隔离及其熔断器
>
> 5、Zuul - 服务网关
>
> 6、Stream - 实现MQ的使用
>
> 7、Config - 动态配置
>
> 8、 Sleuth - 服务追踪

## 二、服务的注册与发现-Eureka

### 2.1 引言

> Eureka就是帮助我们维护所有服务的信息，以便服务之间的相互调用

![1605019839175](images/1605019839175.png)

### 2.2 Eureka的快速入门

#### 2.2.1 创建EurekaServer

> 1、创建一个父工程，并且在父工程中指定SpringCloud版本，并且将packaing修改为pom

```xml
<packaging>pom</packaging>

<properties>
   <java.version>1.8</java.version>
   <spring.cloud-version>Hoxton.SR8</spring.cloud-version>
</properties>
<dependencyManagement>
   <dependencies>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring.cloud-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

> 2、创建eureka的server，创建springboot工程，并且导入依赖，再启动类中添`@EnableEurekaServer`注解和编写yml文件

> 2.1、导入依赖

```xml
<dependencies>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
   </dependency>
</dependencies>
```

> 2.2、启动类添加注解

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);
    }
}
```

> 2.3 编写yml配置文件

```yml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    #当前的eureka是单机版的
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```



#### 2.2.2 创建EurekaClient

> 1、创建Maven工程，修改为SpringBoot

> 2、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> 3、在启动类上添加注解`@EnableEurekaClient`

```java
@EnableEurekaClient
@SpringBootApplication
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class,args);
    }
}
```

> 4、编写配置文件

```yml
#指定Eureka服务地址
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

#指定服务名称
spring:
  application:
    name: CUSTOMER
```

![image-20201111095518705](images/image-20201111095518705.png)

#### 2.2.3 测试Eureka

> 1、创建了一个Search搜索模块，并且注册到Eureka

> 2、使用EurekaClient的对象获取服务信息

```java
@Autowired
    private EurekaClient eurekaClient;
```

> 3、创建RestTemplate

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public  RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

> 4、使用RestTemplate调用

```java
@GetMapping("/customer")
public String customer() {
    //1. 通过eurekaClient 获取到SEARCH服务的信息
    InstanceInfo info = eurekaClient.getNextServerFromEureka("SEARCH", false);
    //2. 获取到访问的地址
    String url = info.getHomePageUrl();
    System.out.println(url);
    //3. 通过restTemplate访问
    String result = restTemplate.getForObject(url + "/search", String.class);
    //4. 返回
    return result;
}
```

### 2.3 Eureka的安全性

> 实现Eureka认证

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

> 2、编写配置类

```java
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //忽略掉/eureka/**路径
        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
```

> 3、编写配置文件,配置用户密码

```yml
#指定用户名密码
spring:
  security:
    user:
      name: root
      password: root
```

> 4、其他服务想注册到Eureka上需要添加用户名密码

```yml
#指定Eureka服务地址
eureka:
  client:
    service-url:
      defaultZone: http://用户名:密码@localhost:8761/eureka
```

### 2.4 Eureka的高可用性

> 如果程序正在运行，突然Eureka宕机了
>
> 1、如果调用方访问过一次被调用方，Eureka的宕机就不会影响到功能
>
> 2、如果调用方没有访问过被调用方，Eureka的宕机就会造成当前功能的不可用到功能

> 搭建Eureka高可用

![image-20201111110217411](images/image-20201111110217411.png)

> 1、准备多态Eureka
>
> 采用了复制的方式，删除iml和target文件，并且修改pom.xml中的项目名称，再给负公差添加module

> 2、让服务注册到多台Eureka上

```yml
server:
  port: 8761

eureka:
  client:
    serviceUrl:
      defaultZone: http://root:root@localhost:8762/eureka/
```

```yml
server:
  port: 8762

eureka:
  client:
    serviceUrl:
      defaultZone: http://root:root@localhost:8761/eureka/

```

> 3、让多台Eureka之间相互通讯

```yml
eureka:
  client:
    #当前的eureka是单机版的 false单机版  true集群
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
      defaultZone: http://root:root@localhost:8761/eureka/
```

### 2.5 Eureka的细节

>1、EurekaClient启动时，讲自己的信息注册到EurekaServer上，EurekaServer就会储存EurekaClient的注册信息。

> 2、当EurekaClient调用服务时，本地没有注册信息的缓存时，去EurekaServer中获取注册信息

> 3、EurekaClient会通过心跳的方式去和EurekaServer进行连接。（默认30s EurekaClient就会发送一次心跳请求，如果超过了90s还没有发送心跳信息的话，EurekaSevrer就认为你宕机了，将当前的EurekaClient从注册表中移除）

```yml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30 #心跳间隔
    lease-expiration-duration-in-seconds: 90 #多久没法送，就认为你宕机了
```

> 4、EurekaClient会每个30s去EurekaServer中去更新本地的注册表

```yml
eureka:
  client:
  	#每隔多久去更新一下本地的注册表缓存信息
    registry-fetch-interval-seconds: 30
```

> 5、Eureka的自我保护机制，统计15分钟内，如果一个服务的心跳发送比例低于85%，EurekaServer就会开启自我保护机制
>
> ​	1、不会从EurekaServer中去移除长时间没有收到心跳的服务
>
> ​	2、EurekaServer还是可以正常提供服务的
>
> ​	3、网络稳定时，EurekaServer才会开始将自己的信息被其他节点同步过去

![image-20201111132523595](images/image-20201111132523595.png)

```yml
eureka:
  # Eureka保护机制配置
  server:
  	#true 开启  false关闭
    enable-self-preservation: true  
```

> 6、CAP定理，C-一致性  A-可用性  P-分区容错性，这三个特新在分布是环境下，只能满足2个，而且分区容错性在分布式环境下，时必须要满足的。只能在AC之间进行权衡。
>
> 1、如果选择CP，保证了一致性，可能会造成你系统在一定时间内是不可以的，如果你同步数据的时间比较长，造成的损失就越大。
>
> 2、如果选择AP的效果，高可用的集群，Eureka集群是无中心，Eureka即便宕机几个也不会影响系统的使用，不需要重新去枚举一个master，也会导致一定时间内数据是不一致。

## 三、服务间的负载均衡-Robbin

### 3.1 引言

> Robbin是帮助我们实现服务和服务负载均衡
>
> 客户端负载均衡：customer客户端模块，将2个Search模块信息全部拉取到本地的缓存，在customer中自己做一个负载均衡的策略，选中某一个服务。
>
> 服务端负载均衡：在注册中心中，直接根据你指定的负载均衡策略，帮你选中一个指定的服务器信息，并返回。

![image-20201111134022333](images/image-20201111134022333.png)

### 3.2 Robbin的快速入门

> 1、启动两个Search模块

> 2、在customer导入robbin依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

> 3、配置整合RestTemplate和Robbin

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public   RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

> 4、在customer中去访问Search

```java
@GetMapping("/customer")
public String customer() {
    String result = restTemplate.getForObject("http://SEARCH/search", String.class);
    //4. 返回
    return result;
}
```

### 3.3 Robbin配置负载均衡策略

> 1、负载均衡策略
>
> 	1. RandomRule：随机策略
>  	2. RoundRobbinRule：轮询策略
>  	3. WeightedResponseTimeRule：默认会采用轮询的策略，后续会根据服务的响应时间，自动给你分配权重
>  	4. BestAvailableRule：根据被调用方并发数最小的去分配

> 2、采用注解的形式

```java
@Bean
public IRule robbinRule() {
    return new RandomRule();
}
```

> 3、配置文件去指定负载均衡的策略（推荐）

```yml
#指定具体服务的负载均衡策略
SEARCH:        #编写服务名称
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule  #具体负载均衡使用的类
```

## 四、服务间的调用-Feign

### 4.1 引言

> Feign可以帮助我们实现面向接口编程，就直接调用其他服务，简化开发。

### 4.2 Feign的快速入门

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

> 2、添加一个注解`@EnableFeignClients`

```java
@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class,args);
    }
}
```

> 3、创建一个接口，并且和search模块做映射

```java
//指定服务名称
@FeignClient("SEARCH")
public interface SearchClient {

    //value -> 目标服务的请求路径，method -> 映射请求方式
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String search();
}
```

> 4、测试使用

```java
@Autowired
private SearchClient searchClient;

@GetMapping("/customer")
public String customer() {    
    String result = searchClient.search();
    return result;
}
```

### 4.3 Feign的传递参数方式

> 1、注意事项
>
> 	1. 如果你传递的参数，比较复杂时，默认会采用POST的请求方式。
>  	2. 传递单个参数时，推荐使用@PathVariable，如果传递的单个参数比较多，这里也可以采用@RequestParam，不要省略value属性
>  	3. 传递对象信息时，统一采用json的方式，添加@RequestBody
>  	4. Client接口必须采用@RequestMapping

> 2、在Search模块下准备三个接口

```java
@GetMapping("/search/{id}")
public Customer findById(@PathVariable Integer id) {
    return new Customer(1, "zhangsan", 23);
}

@GetMapping("/getCustomer")
public Customer getcustomer(@RequestParam Integer id, @RequestParam String name) {
    return new Customer(id, name, 23);
}

@PostMapping("save") 
public Customer save(@RequestBody Customer customer) {
    return customer;
}
```

> 3、封装Customer模块的Controller

```java
@GetMapping("/customer/{id}")
public Customer findById(@PathVariable Integer id) {
    return searchClient.findById(id);
}

@GetMapping("/getCustomer")
public Customer getcustomer(@RequestParam Integer id, @RequestParam String name) {
    return searchClient.getcustomer(id,name);
}

@GetMapping("save")
public Customer save(Customer customer) {
    return searchClient.save(customer);
}
```

> 4、再封装Client接口

```java
@RequestMapping(value = "/search/{id}",method = RequestMethod.GET)
Customer findById(@PathVariable(value = "id") Integer id);

@RequestMapping(value = "/getCustomer",method = RequestMethod.GET)
Customer getcustomer(@RequestParam(value = "id") Integer id, @RequestParam(value = "name") String name);

@RequestMapping(value = "save",method = RequestMethod.GET) //会自动转化成POST请求  405
Customer save(@RequestBody Customer customer);
```

> 5、测试

![image-20201111155409030](images/image-20201111155409030.png)

### 4.4 Feign的Fallback

> Fallback可以帮助我们在使用Feign去调用另一个服务时，如果出现了问题，走服务降级，返回一个错误的数据，避免功能因为一个服务出现问题，全部失效

> 1、创建一个POJO类，实现Client接口

```java
@Component
public class SearchClientFallBack implements SearchClient {
    @Override
    public String search() {
        return "出现问题了";
    }

    @Override
    public Customer findById(Integer id) {
        return null;
    }

    @Override
    public Customer getcustomer(Integer id, String name) {
        return null;
    }

    @Override
    public Customer save(Customer customer) {
        return null;
    }
}
```

> 2、修改Client接口中的注解，添加一个属性

```java
@FeignClient(value = "SEARCH",fallback = SearchClientFallBack.class)
```

> 3、添加一个配置文件。

```yml
#feign和hystrix主件整合
feign:
  hystrix:
    enabled: true
```

> 调用方无法知道具体的错误信息是什么，通过FallBackFactory的方式去实现这个功能

> 1、FallBackFactory基于fallback

> 2、创建一个POJO类，实现FallBackFactory<Client>

```java
@Component
public class SearchClientFallBackFactory implements FallbackFactory<SearchClient> {

    @Autowired
    private SearchClientFallBack searchClientFallBack;
    @Override
    public SearchClient create(Throwable throwable) {
        throwable.printStackTrace();
        return searchClientFallBack;
    }
}
```

> 3、修改Client接口中的属性

```java
@FeignClient(value = "SEARCH",
        fallbackFactory = SearchClientFallBackFactory.class
)
```

## 五、服务的隔离及熔断器-Hystrix

### 5.1 引言

![image-20201111162630381](images/image-20201111162630381.png)

### 5.2 降级机制实现

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

> 2、添加一个注解`@EnableCircuitBreaker`

```java
@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class,args);
    }
}
```

> 3、针对某一个接口去编写他的降级方法

```java
@GetMapping("/customer/{id}")
@HystrixCommand(fallbackMethod ="findByIdFallBack" )
public Customer findById(@PathVariable Integer id) {
    int i = 1/0;
    return searchClient.findById(id);
}

//findById的降级方法，方法描述要与接口一致
public Customer findByIdFallBack(Integer id) {
    return new Customer(-1,"",0);
}
```

> 4、在接口上添加注解

```java
@HystrixCommand(fallbackMethod ="findByIdFallBack" )
```

> 5、测试

![image-20201111164239591](images/image-20201111164239591.png)

### 5.3 线程隔离

> 如果使用Tomcat的线程池去接收用户的请求，使用当前线程去执行其他服务的功能，如果某一个服务出现了故障，导致tomcat的线程大量的堆积，导致tomcat无法处理其他业务功能。
>
> 1、Hystrix线程池（默认），接收用户请求采用tomcat的线程池，执行业务代码，调用其他服务时，采用Hystrix的线程池。
>
> 2、信号量，使用的还是Tomcat的线程池，帮助我们取关了Tomcat的线程池

> 1、Hystrix的线程池的配置(具体的配置属性需要去查看HystrixCommandProperties类)  [wiki](https://github.com/Netflix/Hystrix/wiki/Configuration)
>
> 	1. 线程隔离策略：name = `hystrix.command.default.execution.isolation.strategy`,value=`THREAD`,`SEMAPHORE `
>  	2. 指定超时时间（针对线程池）：name= `hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds` ,value =`1000`
>  	3. 是否开启超时时间配置：name=`hystrix.command.default.execution.timeout.enabled`,value=`true`
>  	4. 超时之后是否中断线程：name=`hystrix.command.default.execution.isolation.thread.interruptOnTimeout`,value=`true`
>  	5. 取消任务之后是否中断线程：name=`hystrix.command.default.execution.isolation.thread.interruptOnCancel`,value=false

> 2、信号量配置信息
>
> 1. 线程隔离策略：name = `hystrix.command.default.execution.isolation.strategy`,value=`THREAD`,`SEMAPHORE `
> 2. 指定信号量的最大并发请求数：name=`hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests`,value=`10`

### 5.4 断路器

#### 5.4.1 断路器介绍

> 如果调用指定服务时，如果说这个服务的失败率达到你输入的阈值麻将断路器从closed状态，转变为open状态，指定服务时无法被访问的，如果你访问就直接走fallback方法，在一定时间内，open状态会再次转变为half open状态，允许一个请求发送到我指定服务，如果成功，则转变为closed，如果失败，服务再次转变为open状态，会再次循环到hald open，直到专路器回到一个closed状态。

![image-20201111181650929](images/image-20201111181650929.png)

#### 5.4.2 配置断路器的监控界面

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

> 2、在启动类中添加注解`@EnableHystrixDashboard`

```java
@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
@ServletComponentScan("cn.zyjblogs.servlet")
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class,args);
    }
}
```

> 3、配置一个Servlet路径，指定上Hystrix的Servlet

```java
@WebServlet("/hystrix.stream")
public class HystrixServlet extends HystrixMetricsStreamServlet {
}
```

> 4、在启动类上添加扫描Servlet的注解`@ServletComponentScan("cn.zyjblogs.servlet")`

> 5、配置文件

```yml
hystrix:
  dashboard:
    proxy-stream-allow-list: localhost
```

> 6、测试

直接访问: http://host:port/hystrix.stream

![1605103421256](images/1605103421256.png)

> 在当前位置输入映射好的servlet路径

![1605103436874](images/1605103436874.png)

#### 5.4.3 配置断路器的属性

> 断路器的属性(10秒之内)
>
> 	1. 断路器的开关：name=` hystrix.command.default.circuitBreaker.enabled `,value=`true`
>  	2. 失败阈值的总请求数：name=` hystrix.command.default.circuitBreaker.requestVolumeThreshold `,value=`20`
>  	3. 请求总数失败率达到%多少时打开断路器：name=` hystrix.command.default.circuitBreaker.errorThresholdPercentage `,value=`50`
>  	4. 断路器open状态后，多少秒是拒绝请求的：name=` hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds `value=`5000`
>  	5. 强制让服务拒绝请求：name=` hystrix.command.default.circuitBreaker.forceOpen `,value=`false`
>  	6. 强制让服务接收请求：name=` hystrix.command.default.circuitBreaker.forceClosed `,value=`false`

> 具体配置方式

```java
@GetMapping("/customer/{id}")
@HystrixCommand(fallbackMethod ="findByIdFallBack",commandProperties = {
    @HystrixProperty(name = "circuitBreaker.enabled",value="true"),
    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value="10"),
    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value="70"),
    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value="5000")
})
```

### 5.5 请求缓存

#### 5.5.1 请求缓存的介绍

> 1、请求缓存的声明周期是一次请求
>
> 2、请求缓存是缓存当前线程中的一个方法，将方法参数作为key，方法的返回结果作为value
>
> 3、在一次请求中，目标方法被调用过一次以后就都会被缓存

![1605104987931](images/1605104987931.png)

#### 5.5.2 请求缓存的实现

> 1、创建一个Service，在Service中调用Search服务

```java
@Service
public class CustomerService {

    @Autowired
    private SearchClient searchClient;

    @CacheResult
    @HystrixCommand(commandKey = "findById")
    public Customer findById(@CacheKey Integer id) throws InterruptedException {
        return searchClient.findById(id);
    }

    @CacheRemove(commandKey = "findById")
    @HystrixCommand
    public void clearFindById(@CacheKey Integer id) {
        System.out.println("findById缓存被清空");
    }

}
```

> 2、使用请求缓存的注解`@CacheResult` `CacheRemove`
>
>  	1. @CacheResult：帮助我们缓存当前方法的返回结果（必须配合@HystrixCommand使用）
>  	2. @CacheRemove：帮助我们清除某一个缓存信息（基于commandKey）
>  	3. @CacheKey：指定那个方法参数作为缓存标识

> 3、修改Search模块的结果返回值

```java
 return new Customer(id, name, (int) (Math.random() * 100000));
```

> 4、编写Filter，去构建HystrixRequestContext

```java
@WebFilter("/*")
public class HystrixRequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HystrixRequestContext.initializeContext();
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
```

> 5、修改Controller

```java
@Autowired 
private CustomerService customerService;

 @GetMapping("/customer/{id}")
  @HystrixCommand(
      fallbackMethod = "findByIdFallBack",
      commandProperties = {
        @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "70"),
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
      })
  public Customer findById(@PathVariable Integer id) throws InterruptedException {
    System.out.println(Thread.currentThread().getName());
    if (id == 1) {
      int i = 1 / 0;
    }
    //一下为添加内容
    System.out.println(customerService.findById(id));
    System.out.println(customerService.findById(id));
    customerService.clearFindById(id);
    System.out.println(customerService.findById(id));
    System.out.println(customerService.findById(id));
    customerService.clearFindById(id);
      
    return searchClient.findById(id);
  }
```

> 6、测试

![1605106759891](images/1605106759891.png)

## 六、服务的网关-Zuul

### 6.1 引言

> 1、客户端维护大量的ip和port信息，直接访问指定服务
>
> 2、认证和授权操作，需要在每一个模块中添加认证和授权操作
>
> 3、项目迭代，服务拆分，服务要合并，需要客户端镜像大量的变化
>
> 4、统一的把安全性校验都放在Zuul中

![网关](images/image-20201112110708902.png)

### 6.2 Zuul的快速入门

> 1、创建Maven项目，修改SpringBoot

> 2、导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> 3、添加注解`@EnableZuulProxy` `@EnableEurekaClient`

```java
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class ZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class,args);
    }
}
```

> 4、编写配置文件

```yml
server:
  port: 80
#指定Eureka服务地址
eureka:
  client:
    service-url:
      defaultZone: http://root:root@localhost:8761/eureka,http://root:root@localhost:8762/eureka

#指定服务名称
spring:
  application:
    name: ZUUL
```

> 5、测试

![image-20201112113609523](images/image-20201112113609523.png)



### 6.3  Zuul常用配置信息

#### 6.3.1 Zuul的监控界面

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 2、编写配置文件

```yml
#查看zuul的监考界面（开发时，配置为*，上线，不要配置）
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

> 3、直接访问:`http://localhost/actuator/routes`

![image-20201112132217601](images/image-20201112132217601.png)

#### 6.3.2 忽略服务设置

```yml
# zuul的配置
zuul:
  #基于服务名忽略服务，无法查看,如果要忽略全部的服务，"*",默认配置的全部路径都会被忽略掉（自定义服务配置，通过这种方式是无法忽略的）
  ignored-services: eureka
  #监考界面依然可以查看，在访问的时候，404无法访问
  ignored-patterns: /**/search/**
```

#### 6.3.3 自定义服务配置

```yml
# zuul的配置
zuul:
  #基于服务名忽略服务，无法查看,如果要忽略全部的服务，"*",默认配置的全部路径都会被忽略掉（自定义服务配置，通过这种方式是无法忽略的）
  ignored-services: "*"
  #监考界面依然可以查看，在访问的时候，404无法访问
  ignored-patterns: /**/search/**
  # 指定自定义服务（方式一，key(服务名):value(路径)）
#  routes:
#    search: /ss/**
#    customer: /cc/**
  # 指定自定义服务（方式二）
  routes:
    kehu: #自定义名称
      path: /cc/**  # 映射路径
      serviceId: customer
```

#### 6.3.4 灰度发布

> 1、添加一个配置类

```java
@Configuration
public class ZuulConfig {
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper(
                "(?<name>^.+)-(?<version>v.+$)",
                "${version}/${name}");
        //服务名-v版本
        //    /v版本/路径
    }
}
```

> 2、准备一个服务，提供2个版本

```yml
version: v1

#指定服务名称
spring:
  application:
    name: CUSTOMER-${version}
```



> 3、修改Zuul的配置

```yml
zuul:
  #基于服务名忽略服务，无法查看,如果需要用到-v的方式，一定要忽略掉
#  ignored-services: "*"
```

> 4、修改CustomerController

```java
@Value("${version}")
  private String version;

@GetMapping("/version")
  public String version() {
    return version;
  }
```



> 5、测试

![image-20201112135410803](images/image-20201112135410803.png)

![image-20201112135429409](images/image-20201112135429409.png)

### 6.4 Zuul的过滤器执行流程

> 客户端请求发送到Zuul服务商，首先通过PreFilter,如果正常放行，会把请求再次转发给RoutingFilter，请求转发到一个指定的服务，在指定的服务响应一个结果之后，再次走一个PostFilter的过滤器链，最终将响应信息返回给客户端。

![image-20201112140312697](images/image-20201112140312697.png)

### 6.5 Zuul过滤器入门

> 1、创建POJO类，继承ZuulFilter

```java
@Component
public class ZuulFilterTest extends ZuulFilter {
   
}

```

> 2、指定当前过滤器的类型

```java
@Override
public String filterType() {
    return FilterConstants.PRE_TYPE;
}  
```

> 3、指定过滤器的执行顺序

```java
@Override
public int filterOrder() {
    return FilterConstants.PRE_DECORATION_FILTER_ORDER -1;
}
```

> 4、配置是否启用

```java
@Override
public boolean shouldFilter() {
    //开启当前过滤器
    return true;
}
```

> 5、指定过滤器中的具体业务代码

```java
@Override
public Object run() throws ZuulException {
    System.out.println("prefix过滤器已经执行~~~");
    return null;
}
```

> 6、测试

![image-20201112141523371](images/image-20201112141523371.png)

### 6.6 PreFilter实现token校验

> 1、准备访问路径，请求参数专递token

`http://localhost/v1/customer/version?token=123`

> 2、创建AuthenticationFilter

```java
@Component
public class AuthenticationFilter extends ZuulFilter{
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //...
        return null;
    }
}
```

> 3、在run方法中编写具体的业务逻辑代码

```java
@Override
    public Object run() throws ZuulException {
        System.out.println("AuthenticationFilter执行了");
        //1. 获取Request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //2. 获取token参数
        String token = request.getParameter("token");
        //3.对比token
        if (token == null || !"123".equalsIgnoreCase(token)) {
            System.out.println("token校验失败");
            //4. token校验失败，直接响应数据
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
```

> 4、测试

![image-20201112145449105](images/image-20201112145449105.png)

### 6.7 Zuul的降级

> 1、创建POJO类，实现接口FallbackProvider

```java
@Component
public class ZuulFallBack implements FallbackProvider {
    
}

```

> 2、重写两个方法

```java
@Override
public String getRoute() {
    //代表指定全部出现问题的服务，都走这个降级方法
    return "*";
}

@Override
public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
    System.out.println("降级的服务"+route);
    cause.printStackTrace();
    return new ClientHttpResponse() {
        @Override
        public HttpHeaders getHeaders() {
            //指定响应头信息
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            //给用户响应的信息
            String msg = "当前服务" + route + "出现问题！！！";
            return new ByteArrayInputStream(msg.getBytes());
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            //指定具体的HttpStatus
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            //返回状态码
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        @Override
        public String getStatusText() throws IOException {
            //指定错误信息
            return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        }

        @Override
        public void close() {

        }
    };
}
```

> 3、测试

![image-20201112151632111](images/image-20201112151632111.png)

### 6.8 Zuul动态路由

> 1、创建一个过滤器

```java
@Component
public class DynamicRoutingFilter  extends ZuulFilter{
    
}
```

> 2、在run方法中编辑业务逻辑

```java
@Override
public String filterType() {
    return FilterConstants.PRE_TYPE;
}

@Override
public int filterOrder() {
    return FilterConstants.PRE_DECORATION_FILTER_ORDER +3;
}

@Override
public boolean shouldFilter() {
    return true;
}

@Override
public Object run() throws ZuulException {
    System.out.println("DynamicRoutingFilter执行了");
    //1、获取Request对象
    RequestContext context = RequestContext.getCurrentContext();
    HttpServletRequest request = context.getRequest();

    //2、获取参数，redisKey
    String redisKey = request.getParameter("redisKey");

    //3、直接判断
    if (redisKey != null && redisKey.equalsIgnoreCase("customer")) {
        System.out.println("DynamicRoutingFilter执行了路由到了/customer");
        //http://localhost:8080/customer
        context.put(FilterConstants.SERVICE_ID_KEY,"customer-v1");
        context.put(FilterConstants.REQUEST_URI_KEY,"/customer");
    } else if (redisKey != null && redisKey.equalsIgnoreCase("search")) {
        System.out.println("DynamicRoutingFilter执行了路由到了/search/1");
        //http://localhost:8081/search
        context.put(FilterConstants.SERVICE_ID_KEY,"search");
        context.put(FilterConstants.REQUEST_URI_KEY,"/search/1");
    }
    return null;
}
```

> 3、测试

![image-20201112155707148](images/image-20201112155707148.png)

![image-20201112155652269](images/image-20201112155652269.png)

## 七、多语言支持-Sidecar

### 7.1 引言

> 在SpringCloud的项目中，需要接入一些非java程序，第三方接口，无法接入eureka，hystrix，feign等组件。启动一个代理的微服务，代理微服务去和非java的程序或第三方接口交流，通过代理的非服务去计入SpringCloud的相关组件。

![image-20201112160258956](images/image-20201112160258956.png)

### 7.2 Sidecar实现

> 1、创建一个第三方的服务

`创建一个SpringBoot工程，并且添加一个Controller`

> 2、创建Maven工程，修改为SpringBoot

> 2、导入以来

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-sidecar</artifactId>
</dependency>
```

> 4、添加注解`@EnableSidecar`

> 5、编写配置文件

```yml
server:
  port: 81

eureka:
  client:
    service-url:
      defaultZone:  http://root:root@localhost:8761/eureka,http://root:root@localhost:8762/eureka

#指定服务名称
spring:
  application:
    name: OTHER-SERVICE    #other-service

# 指定代理的第三方服务
sidecar:
  port: 7001
```

> 6、通过customer通过Feign的方式调用第三方服务

![image-20201112165325023](images/image-20201112165325023.png)

## 八、服务间消息传递-Stream

### 8.1 引言

> 用于构建消息驱动微服务的框架(在下面方便起见也叫它Stream框架)，该框架在Spring Boot的基础上整合了Spring Integration来连接消息代理中间件（RabbitMQ，Kafka等）。它支持多个消息中间件的自定义配置，同时吸收了这些消息中间件的部分概念，例如持久化订阅、消费者分组，和分区等概念。使用Stream框架，我们不必关系如何连接各个消息代理中间件，也不必关系消息的发送与接收，只需要进行简单的配置就可以实现这些功能了，可以让我们更敏捷的进行开发主体业务逻辑了。
>
> **Spring Cloud Stream框架的组成部分：**
>
> 1. Stream框架自己的应用模型；
> 2. 绑定器抽象层，可以与消息代理中间件进行绑定，通过绑定器的API，可实现插件式的绑定器。
> 3. 持久化订阅的支持。
> 4. 消费者组的支持。
> 5. Topic分区的支持。

![image-20201112165641574](images/20190325232054468.png)

### 8.2 Stream快速入门

> 1、启动RabbitMQ

> 2、消费者-导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

> 3、消费者-配置文件

```yml
spring:
  rabbitmq:
    port: 5672
    username: test
    password: test
    virtual-host: /test
```

> 4、消费者-监听的队列

```java
public interface StreamClient {
    @Input("myMessage")
    SubscribableChannel input();
}
```

```java
@Component
@EnableBinding(StreamClient.class)
public class StreamReceiver {
    @StreamListener("myMessage")
    public void msg(Object msg) {
        System.out.println("接收到消息" + msg);
    }
}
```

> 5、启动类添加注解`@EnableBinding(StreamClient.class)`

> 6、生产者-导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

> 7、生产者-配置文件

```yml
spring:
  rabbitmq:
    port: 5672
    username: test
    password: test
    virtual-host: /test
```

> 8、生产者-发布消息

```java
public interface StreamClient {
    @Output("myMessage")
    MessageChannel output();
}
```

```java
@RestController
public class MessageController {
    @Autowired
    private StreamClient streamClient;
    @GetMapping("/send")
    public String send() {
        streamClient.output().send(MessageBuilder.withPayload("Hello Stream").build());
        return "消息发送成功！！";
    }
}
```

> 9、启动类添加注解`@EnableBinding(StreamClient.class)`

> 10、测试访问`http://localhost:8080/send`

![image-20201113093057704](images/image-20201113093057704.png)

![image-20201112173342142](images/image-20201112173342142.png)

### 8.3 Stream重复消费问题

> 只需要添加一个配置，指定消费者组

```yml
spring:
  cloud:
    stream:
      binders:
        myMessage:  #队列名称
          group: customer #消费者组
```

### 8.4 Stream的消费者手动ack

> 1、编写配置

```yml
#指定服务名称
spring:
  cloud:
    stream:
      #实现手动ack
      rabbit:
        bindings:
          myMessage:
            consumer:
              acknowledgeMode: MANUAL
```

> 2、修改消费端方法

```java
@Component
@EnableBinding(StreamClient.class)
public class StreamReceiver {
    @StreamListener("myMessage")
    public void msg(Object msg,
                    @Header(name = AmqpHeaders.CHANNEL) Channel channel,
                    @Header(name = AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws IOException {
        System.out.println("接收到消息" + msg);
        channel.basicAck(deliveryTag,false);
    }
}
```

## 九、服务的动态配置-Config

### 9.1 引言

> 1、配置文件分散在不同项目中的，不方便去维护。
>
> 2、配置文件的安全问题。
>
> 3、修改配置文件，无法立即生效。

![image-20201112180552221](images/image-20201112180552221.png)

### 9.2 搭建Config-Server

> 1、创建Maven工程，修改SpringBoot

> 2、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

> 3、添加注解`@EnableConfigServer`

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class,args);
    }
}
```

> 4、编写配置文件（git）

```yml
spring:
  cloud:
    config:
      server:
        git:
          basedir:     E:\config # 本地仓库地址
          username:    xxxxxx@xxxx.com #远程仓库的用户名
          password:    xxxxxxxx #远程仓库的密码
          uri: https://gitee.com/zyjblog/config-resp.git
```

> 5、测试(例子：`http://localhost:82/master/customer-xxx.yml ` (master可以省略）)
>
> 访问方式如下:
>
> `/{application}/{profile}[/{label}]`
> `/{application}-{profile}.yml`
> `/{label}/{application}-{profile}.yml`  
>
> `/{application}-{profile}.properties`
> `/{label}/{application}-{profile}.properties`

![image-20201113094055276](images/image-20201113094055276.png)

### 9.3、修改Customer连接Config

> 1、导入依赖

```yml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
```

> 2、修改配置文件

```java
#指定Eureka服务地址
eureka:
  client:
    service-url:
      defaultZone: http://root:root@localhost:8761/eureka,http://root:root@localhost:8762/eureka

version: v1
#指定服务名称
spring:
  application:
    name: CUSTOMER-${version}
  cloud:
    config:
      discovery:
        enabled: true
        service-id: CONFIG
      profile: dev
```

> 3、修改配置文件名称`application.yml`改为`bootstrap.yml`

> 4、测试测试发布消息到RabbMQ

![image-20201113102316294](images/image-20201113102316294.png)

### 9.4 实现动态配置

#### 9.4.1 实现原理

![image-20201113103152115](images/image-20201113103152115.png)

#### 9.4.2 服务连接RabbitMQ

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

> 2、编写配置文件

```yml
spring:
  rabbitmq:
    virtual-host: /test
    host: localhost
    username: test
    password: test
    port: 5672
```

> 3、测试

![image-20201113104134210](images/image-20201113104134210.png)

#### 9.4.3 实现手动刷新

> 1、导入依赖(两个服务config和customer均添加)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 2、编写配置文件(两个服务config和customer均添加)

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

> 3、为customer添加一个controller,添加注解`@RefreshScope`

```java
@RestController
@RefreshScope
public class CustomerController {

    @Value("${env}")
    private String env;

    @GetMapping("/env")
    public String env() {
        return env;
    }
}
```

> 4、测试
>
> 1. CONFIG在gitee修改之后，自动拉取最新的配置信息。
> 2. 其他模块需要更新的话，手动发送一个POST请求：http://localhost:10000/actuator/bus-refresh ，不重启项目，即可获取最新的配置信息

#### 9.4.4 内网穿透

> 1、内网穿透官网：http://www.ngrok.cc/

> 2、注册登录

> 3、购买免费隧道，并配置

![image-20201113113814310](images/image-20201113113814310.png)

> 4、下载客户端，并复制隧道id，点击运行客户端，复制到客户端中

![image-20201113114050201](images/image-20201113114050201.png)

> 5、测试访问是否成功

![image-20201113114131931](images/image-20201113114131931.png)

#### 9.4.5 实现自动刷新配置

> 1、配置Gitee中的WebHooks

![image-20201113120733998](images/image-20201113120733998.png)

> 2、给Config添加一个过滤器UrlFilter

```java

@WebFilter("/*")
public  class UrlFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
        String url=httpServletRequest.getRequestURI();
        System.out.println(url);
        if(!url.endsWith("/actuator/bus-refresh")){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        String body=(httpServletRequest).toString();
        System.out.println("original body: "+ body);
        RequestWrapper requestWrapper=new RequestWrapper(httpServletRequest);
        filterChain.doFilter(requestWrapper,servletResponse);
    }
    private class RequestWrapper extends HttpServletRequestWrapper {
        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            byte[] bytes = new byte[0];
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ServletInputStream servletInputStream = new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.read() == -1 ? true : false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {

                }
            };
            return servletInputStream;
        }
    }
}
```

> 3、添加注解`@ServletComponentScan("cn.zyjblogs.filter")`

```java
@SpringBootApplication
@EnableConfigServer
@ServletComponentScan("cn.zyjblogs.filter")
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class,args);
    }
}
```

> 3、测试

![image-20201113121035781](images/image-20201113121035781.png)

## 十、服务的追踪-Sleuth

### 10.1 引言

> 在整个微服务架构中，微服务很多，一个请求可能需要调用很多很多的服务，最终才能完成一个功能，如果说，整个功能出现了问题，在这么多的服务中，如何区定位到问题的所在点，出现问题的原因是什么。
>
> 1、Sleuth可以获取得到整个服务链路的信息
>
> 2、Zipkin通过图形化界面去看到信息。
>
> 3、Sleuth将日志信息存储到数据库中

![image-20201113131325996](images/image-20201113131325996.png)

### 10.2 Sleuth使用

> 1、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

> 2、编写配置文件

```yml
logging:
  level:
    org.springframework.web.servlet.DispatcherServlet: DEBUG
```

> 3、测试
>
> SEARCH：服务名称
>
> 012 总链路id
>
> b0e：当前服务的链路id
>
> false：不会将当前的日志信息，输出到其他系统中

![image-20201113133658258](images/image-20201113133658258.png)

![image-20201113133713872](images/image-20201113133713872.png)

![image-20201113133745985](images/image-20201113133745985.png)

### 10.3 Zipkin的使用

> 1、搭建Zipkin的web工程 https://zipkin.io/pages/quickstart
>
> 1. docker安装Zipkin

1、使用docker pull拉取

```bash
docker pull openzipkin/zipkin
```

2、使用docker-compose

```yml
version: "3.1"
services:
  zipkin:
    image: daocloud.io/daocloud/zipkin:latest
    restart: always
    container_name: zipkin
    ports:
    - 9411:9411
```

```bash
docker-compose up -d  #启动
docker-compose down   #关闭
```

> 2、导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

> 3、编写配置文件

```yml
spring:
  sleuth:
    sampler:
      probability: 1 #百分之多少的sleuth信息需要输出到zipkin
  zipkin:
    base-url: http://127.0.0.1:9411/  #指定zipkin的地址
```

> 4、测试

![image-20201113153443738](images/image-20201113153443738.png)

### 10.4 整合RabbitMQ

> 1、导入RabbitMQ依赖（zipkin中已经依赖了RabbitMQ了）

> 2、修改配置文件

```yml
spring:
  zipkin:
    sender:
      type: rabbit
```



> 3、修改Zipkin信息

```yml
version: "3.1"
services:
  zipkin:
    image: daocloud.io/daocloud/zipkin:latest
    #image: docker.io/openzipkin/zipkin:latest
    restart: always
    container_name: zipkin
    ports:
    - 9411:9411
    environment:
      - RABBIT_ADDRESSES=10.27.10.123:5672 #本地ipv4地址:端口
      - RABBIT_USER=test
      - RABBIT_PASSWORD=test
      - RABBIT_VIRTUAL_HOST=/test
```

> 3、测试

![image-20201113160658535](images/image-20201113160658535.png)

![image-20201113160724897](images/image-20201113160724897.png)

### 10.5  Zipkin存储数据到ES

> 1、重新修改zipkin的文件yml文件

```yml
version: "3.1"
services:
  zipkin:
    image: daocloud.io/daocloud/zipkin:latest
    #image: docker.io/openzipkin/zipkin:latest
    restart: always
    container_name: zipkin
    ports:
    - 9411:9411
    environment:
      - RABBIT_ADDRESSES=10.27.10.123:5672
      - RABBIT_USER=test
      - RABBIT_PASSWORD=test
      - RABBIT_VIRTUAL_HOST=/test
      - STORAGE_TYPE=elasticsearch
      - ES_HOSTS=http://10.27.10.123:9200
```

> 2、安装Es

```bash
 #拉取镜像
 docker pull elasticsearch
 #启动参数
 docker run --name es1_6.6.0 \
-p 9200:9200 \
-p 9300:9300 \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \ 
-v /d/elasticsearch/config/es1.yml:/usr/share/elasticsearch/config/elasticsearch.yml \ 
-v /d/elasticsearch/data/es1:/usr/share/elasticsearch/data \ 
-v /d/elasticsearch/logs/es1:/usr/share/elasticsearch/logs \
-d 13aa43015aa1
```

> 3、安装kibana

```bash
#拉取kibana
docker pull kibana
#启动参数
docker run --name kibana6.6.0 -e ELASTICSEARCH_URL=http://10.27.10.123:9200 -p 5601:5601 -d dfc685453eaa
```

创建索引

![image-20201113164636525](images/image-20201113164636525.png)

> 4、重启zipkin后数据未丢失

```bash
docker-compose restart
```

> 5、测试

![image-20201113164930192](images/image-20201113164930192.png)

## 十一、完整SpringCloud架构图

![image-20201113165759501](images/image-20201113165759501.png)