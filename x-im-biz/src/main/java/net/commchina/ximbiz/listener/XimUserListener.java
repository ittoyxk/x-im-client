package net.commchina.ximbiz.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.util.SpringContextUtil;
import net.commchina.xim.common.constant.AppConstant;
import org.jim.core.ImChannelContext;
import org.jim.core.exception.ImException;
import org.jim.core.packets.User;
import org.jim.server.listener.AbstractImUserListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author xiaokang
 * @Desc
 * @date 2020-05-02 18:18
 */
@Slf4j
public class XimUserListener extends AbstractImUserListener {


    private RabbitTemplate rabbitTemplate;

    public XimUserListener()
    {
        this.rabbitTemplate = SpringContextUtil.getBean(RabbitTemplate.class);
    }

    @Override
    public void doAfterBind(ImChannelContext imChannelContext, User user) throws ImException
    {
        JSONObject  push=new JSONObject();
        push.put("user",user);
        push.put("sessionId",imChannelContext.getSessionContext().getId());
        String stats =push.toJSONString();
        log.info("绑定用户:{}", stats);
        rabbitTemplate.convertAndSend(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, AppConstant.IM_CHAT_USER_KEY,stats);
    }

    @Override
    public void doAfterUnbind(ImChannelContext imChannelContext, User user) throws ImException
    {
        JSONObject  push=new JSONObject();
        push.put("user",user);
        push.put("sessionId",imChannelContext.getSessionContext().getId());
        String stats =push.toJSONString();
        log.info("解绑用户:{}", stats);
        rabbitTemplate.convertAndSend(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, AppConstant.IM_CHAT_USER_KEY,stats);
    }
}
