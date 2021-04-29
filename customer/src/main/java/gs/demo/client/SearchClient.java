package gs.demo.client;

import gs.demo.entity.Customer;
import gs.demo.factory.SearchClientFallBackFactory;
import gs.demo.pojo.SearchClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author grassPrince
 * @Date 2021/4/29 9:23
 * @Description 针对search服务的接口
 **/
@FeignClient(
        value = "search",
        /*fallback = SearchClientFallBack.class,*/
        fallbackFactory = SearchClientFallBackFactory.class)
public interface SearchClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    String search();

    @RequestMapping(method = RequestMethod.GET, value = "/search/{id}")
    Customer findById(@PathVariable(value = "id") Integer id);

    @RequestMapping(method = RequestMethod.GET, value = "/getCustomer")
    Customer getCustomer(@RequestParam(value = "id") Integer id, @RequestParam(value = "name") String name);

    // 当有复杂类型时， 会自动将GET转成POST请求
    @RequestMapping(method = RequestMethod.GET, value = "/save")
    Customer save(@RequestBody Customer customer);

}
