package gs.demo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author grassPrince
 * @Date 2021/4/30 16:36
 * @Description TODO
 **/
public interface StreamClient {

    @Input("myMessage")
    SubscribableChannel input();

}
