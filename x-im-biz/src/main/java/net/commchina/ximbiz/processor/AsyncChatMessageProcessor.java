package net.commchina.ximbiz.processor;

import org.jim.core.packets.ChatBody;
import org.jim.server.processor.chat.BaseAsyncChatMessageProcessor;

import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 12:52
 */
public abstract class AsyncChatMessageProcessor extends BaseAsyncChatMessageProcessor {

    /**
     * 消息持久化写入
     *
     * @param key      持久化ID
     * @param score    持久化时间戳
     * @param chatBody 消息记录
     */
    public abstract void writeMessage(String key, double score, ChatBody chatBody);

    /**
     * 删除消息持久化
     *
     * @param key 持久化ID
     */
    public abstract void remove(String key);

    /**
     * 模糊删除消息持久化
     *
     * @param key 持久化ID
     */
    public abstract void fuzzyRemove(String key);


    /**
     * 获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    public abstract List<ChatBody> getOfflineMessage(String key);

    /**
     * 模糊获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    public abstract List<ChatBody> fuzzyOfflineMessage(String key);

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @return
     */
    public abstract List<ChatBody> getHistoryMessage(String key);

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @param min 消息区间开始时间
     * @param max 消息区间结束时间
     * @return
     */
    public abstract List<ChatBody> getHistoryMessage(String key, double min, double max);

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
    public abstract List<ChatBody> getHistoryMessage(String key, double min, double max, int offset, int count);
}
