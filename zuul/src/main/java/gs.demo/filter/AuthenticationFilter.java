package gs.demo.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author grassPrince
 * @Date 2021/4/30 11:35
 * @Description token认证拦截器
 **/
@Component
public class AuthenticationFilter extends ZuulFilter {

    @Override
    public String filterType() {
        // 指定过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 过滤器执行顺序：官方推荐方式
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        // 是否开启过滤器
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 过滤器实际操作
        // 获取Request对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        // 获取token
        String token = request.getParameter("token");

        // 判断token
        if (StringUtils.isEmpty(token) || !"abc123".equals(token)) {
            // 校验失败的处理：filter仍会继续处理，不调用服务的具体方法
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        // 验证成功无需操作
        return null;
    }
}
