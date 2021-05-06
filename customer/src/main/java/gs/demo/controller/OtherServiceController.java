package gs.demo.controller;

import gs.demo.client.OtherServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author grassPrince
 * @Date 2021/4/30 15:49
 * @Description 由sidecar处理的第三方接口
 **/
@RestController
public class OtherServiceController {

    @Autowired
    private OtherServiceClient otherServiceClient;

    @GetMapping("list")
    public String list() {
        return otherServiceClient.list();
    }

}
