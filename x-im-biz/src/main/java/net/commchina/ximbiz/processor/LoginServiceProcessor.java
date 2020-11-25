/**
 *
 */
package net.commchina.ximbiz.processor;

import com.alibaba.fastjson.JSONObject;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.bean.userinfo.UserInfo;
import net.commchina.framework.common.bean.userinfo.UserInfoContext;
import net.commchina.framework.common.remote.OauthClient;
import net.commchina.framework.common.response.APIResponse;
import net.commchina.framework.common.util.SpringContextUtil;
import org.apache.commons.io.IOUtils;
import org.jim.core.ImChannelContext;
import org.jim.core.packets.*;
import org.jim.server.processor.login.LoginCmdProcessor;
import org.jim.server.protocol.AbstractProtocolCmdProcessor;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaokang
 *
 */
@Slf4j
public abstract class LoginServiceProcessor extends AbstractProtocolCmdProcessor implements LoginCmdProcessor {

    /**
     * 根据用户名和密码获取用户
     * @param loginReqBody
     * @param imChannelContext
     * @return
     * @author: xiaokang
     */
    @Override
    public User getUser(LoginReqBody loginReqBody, ImChannelContext imChannelContext)
    {
        User user = getUser(imChannelContext);
        return user;
    }


    /**
     * 登陆成功返回状态码:ImStatus.C10007
     * 登录失败返回状态码:ImStatus.C10008
     * 注意：只要返回非成功状态码(ImStatus.C10007),其他状态码均为失败,此时用户可以自定义返回状态码，定义返回前端失败信息
     */
    @Override
    public LoginRespBody doLogin(LoginReqBody loginReqBody, ImChannelContext imChannelContext)
    {
        if (Objects.nonNull(loginReqBody.getToken())) {
            UserInfo user = authUser(loginReqBody.getToken(),loginReqBody.getUserId());
            if (user != null) {
                imChannelContext.setUserId(user.getUserId().toString());
                imChannelContext.setAttribute("userId", user.getUserId().toString());
                imChannelContext.setAttribute("userName", user.getUserName());
                return LoginRespBody.success();
            }
        }
        return LoginRespBody.failed();
    }

    @Override
    public void onSuccess(User user, ImChannelContext channelContext)
    {
        log.info("登录成功回调方法");
    }

    @Override
    public void onFailed(ImChannelContext imChannelContext)
    {
        log.info("登录失败回调方法");
    }

    /**
     * 查询当前用户所在群组
     * @param userId
     * @return
     */
    public abstract List<Group> getGroups(String userId);

    /**
     * 查询用户详情
     * @param userId
     * @return
     */
    public abstract User getUserInfo(String userId);


	/**
	 * 查询好友
	 * @param userId
	 * @return
	 */
	public abstract Group initFriends(String userId);

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     * @author: xiaokang
     */
    public User getUser(ImChannelContext imChannelContext)
    {
        User.Builder builder = User.newBuilder()
                .userId(imChannelContext.getUserId())
                .nick(imChannelContext.getAttribute("userName").toString());
        //模拟的群组,正式根据业务去查数据库或者缓存;
        List<Group> groups = getGroups(imChannelContext.getUserId());
        groups.stream().forEach(g -> builder.addGroup(g));
        //模拟的用户好友,正式根据业务去查数据库或者缓存;
		Group friends = initFriends(imChannelContext.getUserId());
		User userInfo = getUserInfo(imChannelContext.getUserId());
		builder.addFriend(friends).avatar(userInfo.getAvatar()).status(UserStatusType.ONLINE.getStatus()).sign(userInfo.getSign());
        return builder.build();
    }

    protected UserInfo authUser(String token,String userId)
    {
        UserInfo data = null;
        OauthClient oauthClient = SpringContextUtil.getBean(OauthClient.class);
        UserInfo userInfo=new UserInfo();
        userInfo.setRequestId(userId);
        UserInfoContext.setUserInfo(userInfo);
        Response authResp = oauthClient.userAuth(token);
        if (authResp.status() == HttpStatus.OK.value()) {
            try {
                String bodyStr = IOUtils.toString(authResp.body().asInputStream(), "UTF-8");
                APIResponse apiResponse = (APIResponse) JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), APIResponse.class);
                if (apiResponse.getCode() == 1) {
                    data = JSONObject.toJavaObject((JSONObject) apiResponse.getData(), UserInfo.class);
                }
            } catch (IOException e) {
                log.error("E:{}", e);
            }
        }
        return data;
    }
}
