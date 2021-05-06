package gs.demo.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @Author grassPrince
 * @Date 2021/4/30 17:13
 * @Description TODO
 **/
public interface StreamClient {

    @Output("myMessage")
    MessageChannel output();

}
