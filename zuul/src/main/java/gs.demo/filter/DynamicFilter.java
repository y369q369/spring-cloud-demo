package gs.demo.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author grassPrince
 * @Date 2021/4/30 15:00
 * @Description 动态路由过滤器
 **/
@Component
public class DynamicFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 过滤器实际操作
        // 获取Request对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        // 获取参数
        String redisKey = request.getParameter("redisKey");

        // 模拟动态路由操作：从redis获取路由
        if (StringUtils.isNotEmpty(redisKey) && redisKey.equalsIgnoreCase("cus")) {
            context.put(FilterConstants.SERVICE_ID_KEY, "customer-v1");
            context.put(FilterConstants.REQUEST_URI_KEY, "/customer");
        }

        return null;
    }
}
