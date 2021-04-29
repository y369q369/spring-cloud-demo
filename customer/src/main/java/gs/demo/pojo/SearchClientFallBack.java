package gs.demo.pojo;

import gs.demo.client.SearchClient;
import gs.demo.entity.Customer;
import org.springframework.stereotype.Component;

/**
 * @Author grassPrince
 * @Date 2021/4/29 10:26
 * @Description SearchClient接口中出现问题时的返回值处理
 **/
@Component
public class SearchClientFallBack implements SearchClient {

    @Override
    public String search() {
        return "出现问题了";
    }

    @Override
    public Customer findById(Integer id) {
        return null;
    }

    @Override
    public Customer getCustomer(Integer id, String name) {
        return null;
    }

    @Override
    public Customer save(Customer customer) {
        return null;
    }
}
