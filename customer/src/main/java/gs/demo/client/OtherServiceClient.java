package gs.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author grassPrince
 * @Date 2021/4/30 15:50
 * @Description Feign的客户端：第三方程序
 **/
@FeignClient("sidecar")
public interface OtherServiceClient {

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list();

}
