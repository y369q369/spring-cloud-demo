package gs.demo.controller;

import gs.demo.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

/**
 * @Author grassPrince
 * @Date 2021/4/28 17:34
 * @Description TODO
 **/
@RestController
public class SearchController {

    @Value("${server.port}")
    private String port;

    @GetMapping("search")
    public String search() {
        int i = 1/0;
        return "search - " + port + " : " + String.valueOf(new SecureRandom().nextDouble());
    }

    @GetMapping("/search/{id}")
    public Customer findById(@PathVariable Integer id) {
        return new Customer(id, "hh", new SecureRandom().nextInt(50));
    }

    @GetMapping("/getCustomer")
    public Customer getCustomer(@RequestParam Integer id, @RequestParam String name) {
        return new Customer(id, name, new SecureRandom().nextInt(50));
    }

    @PostMapping("/save")
    public Customer save(@RequestBody Customer customer) {
        return customer;
    }

}
