package gs.demo.otherservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author grassPrince
 * @Date 2021/4/30 15:30
 * @Description TODO
 **/
@RestController
public class MyController {

    @GetMapping("list")
    public String list() {
        return "other-service: list";
    }

}
