package net.commchina.ximbiz.service;

import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.util.SpringContextUtil;
import net.commchina.xim.common.bean.ChatMessageReq;
import net.commchina.xim.common.constant.AppConstant;
import net.commchina.ximbiz.processor.AsyncChatMessageProcessor;
import net.commchina.ximbiz.remote.ImCoreRemote;
import org.jim.core.ImChannelContext;
import org.jim.core.packets.ChatBody;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 13:07
 */
@Slf4j
public class XimAsyncChatMessageProcessor extends AsyncChatMessageProcessor {

    private RabbitTemplate rabbitTemplate;

    private ImCoreRemote imCoreRemote;

    public XimAsyncChatMessageProcessor()
    {
        this.rabbitTemplate = SpringContextUtil.getBean(RabbitTemplate.class);
        this.imCoreRemote = SpringContextUtil.getBean(ImCoreRemote.class);
    }

    /**
     * 供子类拿到消息进行业务处理(如:消息持久化到数据库等)的抽象方法
     *
     * @param chatBody
     * @param imChannelContext
     */
    @Override
    protected void doProcess(ChatBody chatBody, ImChannelContext imChannelContext)
    {
    }

    /**
     * 消息持久化写入
     *
     * @param key      持久化ID
     * @param score    持久化时间戳
     * @param chatBody 消息记录
     */
    @Override
    public void writeMessage(String key, double score, ChatBody chatBody)
    {
        ChatMessageReq build = ChatMessageReq.builder().msgType(1).key(key).score(score).chatBody(chatBody).build();
        rabbitTemplate.convertAndSend(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, AppConstant.IM_CHAT_MESSAGE_ROUTING_KEY, build);
    }

    /**
     * 删除消息持久化
     *
     * @param key 持久化ID
     */
    @Override
    public void remove(String key)
    {
        ChatMessageReq build = ChatMessageReq.builder().msgType(2).key(key).build();
        rabbitTemplate.convertAndSend(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, AppConstant.IM_CHAT_MESSAGE_ROUTING_KEY, build);
    }

    /**
     * 模糊删除消息持久化
     *
     * @param key 持久化ID
     */
    @Override
    public void fuzzyRemove(String key)
    {
        ChatMessageReq build = ChatMessageReq.builder().msgType(3).key(key).build();
        rabbitTemplate.convertAndSend(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, AppConstant.IM_CHAT_MESSAGE_ROUTING_KEY, build);
    }

    /**
     * 获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    @Override
    public List<ChatBody> getOfflineMessage(String key)
    {
        return imCoreRemote.getOfflineMessage(key).getData();
    }

    /**
     * 模糊获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    @Override
    public List<ChatBody> fuzzyOfflineMessage(String key)
    {
        return imCoreRemote.fuzzyOfflineMessage(key).getData();
    }

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @return
     */
    @Override
    public List<ChatBody> getHistoryMessage(String key)
    {
        return imCoreRemote.getHistoryMessage(key).getData();
    }

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @param min 消息区间开始时间
     * @param max 消息区间结束时间
     * @return
     */
    @Override
    public List<ChatBody> getHistoryMessage(String key, double min, double max)
    {
        return imCoreRemote.getHistoryMessage(key, min, max).getData();
    }

    /**
     * 获取与指定用户历史消息;
     *
     * @param key    持久化ID
     * @param min    消息区间开始时间
     * @param max    消息区间结束时间
     * @param offset 分页偏移量
     * @param count  数量
     * @return
     */
    @Override
    public List<ChatBody> getHistoryMessage(String key, double min, double max, int offset, int count)
    {
        return imCoreRemote.getHistoryMessage(key, min, max, offset, count).getData();
    }
}
