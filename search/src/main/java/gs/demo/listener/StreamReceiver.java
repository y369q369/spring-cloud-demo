package gs.demo.listener;

import gs.demo.stream.StreamClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @Author grassPrince
 * @Date 2021/4/30 16:38
 * @Description TODO
 **/
@Component
@EnableBinding(StreamClient.class)
public class StreamReceiver {

    @StreamListener("myMessage")
    public void msg(Object msg) {
        System.out.println("接收到消息： " + msg);
    }

}
