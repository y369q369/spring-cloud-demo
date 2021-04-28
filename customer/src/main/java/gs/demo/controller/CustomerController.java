package gs.demo.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    @GetMapping("customer")
    public String customer() {
        // 获取search的服务信息
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("search", false);
        // 获取search访问地址
        String url = instanceInfo.getHomePageUrl();
        System.out.println(url);
        String result = restTemplate.getForObject(url + "/search", String.class);
        return result;
    }

}
