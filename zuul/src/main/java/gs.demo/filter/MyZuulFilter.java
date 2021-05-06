package gs.demo.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @Author grassPrince
 * @Date 2021/4/30 11:35
 * @Description 自定义Zuul过滤器
 **/
@Component
public class MyZuulFilter extends ZuulFilter {

    @Override
    public String filterType() {
        // 指定过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 过滤器执行顺序：官方推荐方式
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // 是否开启过滤器
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 过滤器实际操作
        System.out.println("preSuulFilter已执行");
        return null;
    }
}
