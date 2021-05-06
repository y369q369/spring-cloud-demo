package gs.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author grassPrince
 * @Date 2021/4/30 10:43
 * @Description TODO
 **/
@RestController
public class VersionController {

    @Value("${version}")
    private String version;

    @GetMapping("version")
    public String version() {
        return "customer-" + version;
    }
}
