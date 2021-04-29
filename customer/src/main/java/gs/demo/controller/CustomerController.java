package gs.demo.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import gs.demo.client.SearchClient;
import gs.demo.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

/**
 * @Author grassPrince
 * @Date 2021/4/28 17:38
 * @Description TODO
 **/
@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private SearchClient searchClient;

    @GetMapping("customer")
    public String customer() {
        // 1、使用Eureka：获取服务信息需通过EurekaClient获取
//        // 获取search的服务信息
//        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("search", false);
//        // 获取search访问地址
//        String url = instanceInfo.getHomePageUrl();
//        System.out.println(url);
//        String result = restTemplate.getForObject(url + "/search", String.class);
//        return result;

//        // 2、使用Ribbon：获取服务信息直接使用RestTemplate即可
//        String result = restTemplate.getForObject( "http://search/search", String.class);

        // 3、使用Feign：直接通过接口访问
        System.out.println(Thread.currentThread().getName());
        String result = searchClient.search();
        return result;
    }

    @GetMapping("/customer/{id}")
    @HystrixCommand(fallbackMethod = "findByIdFallBack", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
    })
    public Customer findById(@PathVariable Integer id) throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(3500);
        return searchClient.findById(id);
    }

    // findById的降级方法
    public Customer findByIdFallBack(Integer id) {
        return new Customer(-1, "", 0);
    }

    @GetMapping("/getCustomer")
    @HystrixCommand(fallbackMethod = "getCustomerFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
    })
    public Customer getCustomer(@RequestParam Integer id, @RequestParam String name) {
        return searchClient.getCustomer(id, name);
    }

    public Customer getCustomerFallBack(Integer id, String name) {
        return searchClient.getCustomer(id, name);
    }

    @GetMapping("/save")
    public Customer save(Customer customer) {
        return searchClient.save(customer);
    }

}
