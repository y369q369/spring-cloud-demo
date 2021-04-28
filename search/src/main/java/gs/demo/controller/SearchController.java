package gs.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

/**
 * @Author grassPrince
 * @Date 2021/4/28 17:34
 * @Description TODO
 **/
@RestController
public class SearchController {

    @GetMapping("search")
    public String search() {
        return "search : " + String.valueOf(new SecureRandom().nextDouble());
    }

}
