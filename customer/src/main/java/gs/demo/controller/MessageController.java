package gs.demo.controller;

import gs.demo.stream.StreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author grassPrince
 * @Date 2021/4/30 17:15
 * @Description TODO
 **/
@RestController
public class MessageController {

    @Autowired
    private StreamClient streamClient;

    @GetMapping("/send")
    public String send(@RequestParam String msg) {
        streamClient.output().send(MessageBuilder.withPayload(msg).build());
        return "success";
    }
}
