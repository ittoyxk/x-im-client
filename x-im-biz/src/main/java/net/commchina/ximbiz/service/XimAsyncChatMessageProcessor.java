package net.commchina.ximbiz.service;

import lombok.extern.slf4j.Slf4j;
import net.commchina.ximbiz.processor.AsyncChatMessageProcessor;
import org.jim.core.ImChannelContext;
import org.jim.core.packets.ChatBody;

import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 13:07
 */
@Slf4j
public class XimAsyncChatMessageProcessor extends AsyncChatMessageProcessor {


    public XimAsyncChatMessageProcessor()
    {
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

    }

    /**
     * 删除消息持久化
     *
     * @param key 持久化ID
     */
    @Override
    public void remove(String key)
    {

    }

    /**
     * 模糊删除消息持久化
     *
     * @param key 持久化ID
     */
    @Override
    public void fuzzyRemove(String key)
    {

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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
    }
}
