package net.commchina.xim.common.aspect;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.bean.userinfo.UserInfoContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: commchina-common
 * @author: hengxiaokang
 * @time 2019/11/14 14:39
 */
@Slf4j
public class HeaderRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template)
    {
        //排除远程服务的认证消息头注入
        if (StringUtils.startsWithAny(template.url(), "/token/getUser")) {
            template.header("Proxy-Client-IP", UserInfoContext.getRequestId());
        }
    }
}
