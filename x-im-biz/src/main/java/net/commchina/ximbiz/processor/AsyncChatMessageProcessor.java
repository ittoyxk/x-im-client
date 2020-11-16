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
     * 获取与指定用户离线消息;
     *
     * @param userId     用户ID
     * @param fromUserId 目标用户ID
     * @return
     */
    public abstract List<ChatBody> getFriendsOfflineMessage(String userId, String fromUserId);

    /**
     * 获取与所有用户离线消息;
     *
     * @param userId 用户ID
     * @return
     */
    public abstract  List<ChatBody> getFriendsOfflineMessage(String userId);

    /**
     * 获取用户指定群组离线消息;
     *
     * @param key  持久化ID
     * @return
     */
    public abstract  List<ChatBody> getGroupOfflineMessage(String key);

    /**
     * 获取与指定用户历史消息;
     *
     * @param userId     用户ID
     * @param fromUserId 目标用户ID
     * @param beginTime  消息区间开始时间
     * @param endTime    消息区间结束时间
     * @param offset     分页偏移量
     * @param count      数量
     * @return
     */
    public abstract  List<ChatBody> getFriendHistoryMessage(String userId, String fromUserId, Double beginTime, Double endTime, Integer offset, Integer count);

    /**
     * 获取与指定群组历史消息;
     *
     * @param userId    用户ID
     * @param groupId   群组ID
     * @param beginTime 消息区间开始时间
     * @param endTime   消息区间结束时间
     * @param offset    分页偏移量
     * @param count     数量
     * @return
     */
    public abstract  List<ChatBody> getGroupHistoryMessage(String userId, String groupId, Double beginTime, Double endTime, Integer offset, Integer count);
}
