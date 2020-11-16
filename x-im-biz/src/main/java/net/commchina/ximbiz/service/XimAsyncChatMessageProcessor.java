package net.commchina.ximbiz.service;

import lombok.extern.slf4j.Slf4j;
import net.commchina.ximbiz.processor.AsyncChatMessageProcessor;
import org.jim.core.ImChannelContext;
import org.jim.core.packets.ChatBody;
import org.jim.core.utils.JsonKit;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 13:07
 */
@Slf4j
public class XimAsyncChatMessageProcessor extends AsyncChatMessageProcessor {
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
     * 获取与指定用户离线消息;
     *
     * @param userId     用户ID
     * @param fromUserId 目标用户ID
     * @return
     */
    @Override
    public List<ChatBody> getFriendsOfflineMessage(String userId, String fromUserId)
    {
        return null;
    }

    /**
     * 获取与所有用户离线消息;
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<ChatBody> getFriendsOfflineMessage(String userId)
    {
        return null;
    }

    /**
     * 获取用户指定群组离线消息;
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return
     */
    @Override
    public   List<ChatBody> getGroupOfflineMessage(String key)
    {
        return null;
    }

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
    @Override
    public List<ChatBody> getFriendHistoryMessage(String userId, String fromUserId, Double beginTime, Double endTime, Integer offset, Integer count)
    {
        return null;
    }

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
    @Override
    public List<ChatBody> getGroupHistoryMessage(String userId, String groupId, Double beginTime, Double endTime, Integer offset, Integer count)
    {
        return null;
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
}
