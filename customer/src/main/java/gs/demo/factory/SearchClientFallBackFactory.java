package gs.demo.factory;

import feign.hystrix.FallbackFactory;
import gs.demo.client.SearchClient;
import gs.demo.pojo.SearchClientFallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author grassPrince
 * @Date 2021/4/29 10:42
 * @Description TODO
 **/
@Component
public class SearchClientFallBackFactory implements FallbackFactory<SearchClient> {

    @Autowired
    private SearchClientFallBack searchClientFallBack;

    @Override
    public SearchClient create(Throwable throwable) {
        throwable.printStackTrace();
        return searchClientFallBack;
    }
}
