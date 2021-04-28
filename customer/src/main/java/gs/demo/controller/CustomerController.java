package gs.demo.controller;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RestTemplate template;

    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping("customer")
    public String customer() {

    }

}