package net.commchina.ximbiz.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jim.core.ImChannelContext;
import org.jim.core.exception.ImException;
import org.jim.core.packets.User;
import org.jim.server.listener.AbstractImUserListener;

/**
 * @author xiaokang
 * @Desc
 * @date 2020-05-02 18:18
 */
@Slf4j
public class XimUserListener extends AbstractImUserListener {


    @Override
    public void doAfterBind(ImChannelContext imChannelContext, User user) throws ImException
    {
        log.info("绑定用户:{}", JSONObject.toJSONString(user));
    }

    @Override
    public void doAfterUnbind(ImChannelContext imChannelContext, User user) throws ImException
    {
        log.info("解绑用户:{}", JSONObject.toJSONString(user));
    }
}
