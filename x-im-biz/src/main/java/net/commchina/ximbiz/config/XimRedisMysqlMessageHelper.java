package net.commchina.ximbiz.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.boot.redis.EnhancedRedisService;
import net.commchina.framework.common.util.SpringContextUtil;
import net.commchina.ximbiz.processor.AsyncChatMessageProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jim.core.config.ImConfig;
import org.jim.core.listener.ImStoreBindListener;
import org.jim.core.message.AbstractMessageHelper;
import org.jim.core.packets.*;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.ChatReqHandler;
import org.jim.server.util.ChatKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/10/20 14:04
 */
@Slf4j
public class XimRedisMysqlMessageHelper extends AbstractMessageHelper {


    private EnhancedRedisService enhancedRedisService;
    private AsyncChatMessageProcessor messageProcessor;
    private static final String SUFFIX = ":";


    public XimRedisMysqlMessageHelper()
    {
        enhancedRedisService = SpringContextUtil.getBean(EnhancedRedisService.class);
        messageProcessor = CommandManager.getCommand(Command.COMMAND_CHAT_REQ, ChatReqHandler.class).getSingleProcessor(AsyncChatMessageProcessor.class);
        this.imConfig = ImConfig.Global.get();
    }

    @Override
    public ImStoreBindListener getBindListener()
    {

        return new XimRedisStoreBindListener(imConfig, this);
    }

    @Override
    public boolean isOnline(String userId)
    {
        try {
            return enhancedRedisService.sIsMember(USER + SUFFIX + TERMINAL + SUFFIX + Protocol.WEB_SOCKET, userId)
                    || enhancedRedisService.sIsMember(USER + SUFFIX + TERMINAL + SUFFIX + Protocol.TCP, userId)
                    || enhancedRedisService.sIsMember(USER + SUFFIX + TERMINAL + SUFFIX + Protocol.HTTP, userId);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    @Override
    public List<String> getGroupUsers(String groupId)
    {
        String groupUserKey = groupId + SUFFIX + USER;
        return XimRedisCacheManager.getCache(GROUP).listGetAll(groupUserKey);
    }


    @Override
    public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody)
    {
        double score = chatBody.getCreateTime();
        messageProcessor.writeMessage(XimRedisCacheManager.getCache(timelineTable).getCacheName() + timelineId, score, chatBody);
    }


    @Override
    public void addGroupUser(String userId, String groupId)
    {
        List<String> users = XimRedisCacheManager.getCache(GROUP).listGetAll(groupId);
        if (users.contains(userId)) {
            return;
        }
        XimRedisCacheManager.getCache(GROUP).listPushTail(groupId, userId);
    }

    @Override
    public void removeGroupUser(String userId, String groupId)
    {
        XimRedisCacheManager.getCache(GROUP).listRemove(groupId, userId);
    }

    @Override
    public UserMessageData getFriendsOfflineMessage(String userId, String fromUserId)
    {
        String userFriendKey = USER + SUFFIX + userId + SUFFIX + fromUserId;
        String key = XimRedisCache.cacheKey(XimRedisCacheManager.getCache(PUSH).getCacheName(), userFriendKey);
        List<ChatBody> messageDataList = messageProcessor.getOfflineMessage(key);
        messageProcessor.remove(key);
        return putFriendsMessage(new UserMessageData(userId), messageDataList, null);
    }

    @Override
    public UserMessageData getFriendsOfflineMessage(String userId)
    {
        UserMessageData messageData = new UserMessageData(userId);
        try {
            //获取好友离线消息;
            List<ChatBody> messageList = new ArrayList<ChatBody>();
            messageList.addAll(messageProcessor.fuzzyOfflineMessage(PUSH + SUFFIX + USER + SUFFIX + userId + "%"));
            messageProcessor.fuzzyRemove(PUSH + SUFFIX + USER + SUFFIX + userId + "%");
            putFriendsMessage(messageData, messageList, null);

            //获取群组离线消息;
            List<ChatBody> groupMessage = messageProcessor.fuzzyOfflineMessage(PUSH + SUFFIX + GROUP + SUFFIX + "%" + SUFFIX + userId);
            messageProcessor.fuzzyRemove(PUSH + SUFFIX + GROUP + SUFFIX + "%" + SUFFIX + userId);
            putGroupMessage(messageData, groupMessage);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return messageData;
    }

    @Override
    public UserMessageData getGroupOfflineMessage(String userId, String groupId)
    {
        UserMessageData messageData = new UserMessageData(userId);
        String userGroupKey = GROUP + SUFFIX + groupId + SUFFIX + userId;
        String key = XimRedisCache.cacheKey(XimRedisCacheManager.getCache(PUSH).getCacheName(), userGroupKey);
        List<ChatBody> messages = messageProcessor.getOfflineMessage(key);
        if (CollectionUtils.isEmpty(messages)) {
            return messageData;
        }
        putGroupMessage(messageData, messages);
        messageProcessor.remove(key);
        return messageData;
    }

    @Override
    public UserMessageData getFriendHistoryMessage(String userId, String fromUserId, Double beginTime, Double endTime, Integer offset, Integer count)
    {
        String sessionId = ChatKit.sessionId(userId, fromUserId);
        String userSessionKey = USER + SUFFIX + sessionId;
        List<ChatBody> messages = getHistoryMessage(userSessionKey, beginTime, endTime, offset, count);
        UserMessageData messageData = new UserMessageData(userId);
        putFriendsMessage(messageData, messages, fromUserId);
        return messageData;
    }

    @Override
    public UserMessageData getGroupHistoryMessage(String userId, String groupId, Double beginTime, Double endTime, Integer offset, Integer count)
    {
        String groupKey = GROUP + SUFFIX + groupId;
        List<ChatBody> messages = getHistoryMessage(groupKey, beginTime, endTime, offset, count);
        UserMessageData messageData = new UserMessageData(userId);
        putGroupMessage(messageData, messages);
        return messageData;
    }

    private List<ChatBody> getHistoryMessage(String historyKey, Double beginTime, Double endTime, Integer offset, Integer count)
    {
        boolean isTimeBetween = (beginTime != null && endTime != null);
        boolean isPage = (offset != null && count != null);
//        XimRedisCache storeCache = XimRedisCacheManager.getCache(STORE);
        //消息区间，不分页
        if (isTimeBetween && !isPage) {
            return messageProcessor.getHistoryMessage(historyKey, beginTime, endTime);
            //消息区间，并且分页;
        } else if (isTimeBetween && isPage) {
            return messageProcessor.getHistoryMessage(historyKey, beginTime, endTime, offset, count);
            //所有消息，并且分页;
        } else if (isPage) {
            return messageProcessor.getHistoryMessage(historyKey, 0, Double.MAX_VALUE, offset, count);
            //所有消息，不分页;
        } else {
            return messageProcessor.getHistoryMessage(historyKey);
        }
    }

    /**
     * 放入用户群组消息;
     *
     * @param userMessage
     * @param messages
     */
    public UserMessageData putGroupMessage(UserMessageData userMessage, List<ChatBody> messages)
    {
        if (Objects.isNull(userMessage) || CollectionUtils.isEmpty(messages)) {
            return userMessage;
        }
        messages.forEach(chatBody -> {
            String groupId = chatBody.getGroupId();
            if (StringUtils.isEmpty(groupId)) {
                return;
            }
            List<ChatBody> groupMessages = userMessage.getGroups().get(groupId);
            if (CollectionUtils.isEmpty(groupMessages)) {
                groupMessages = new ArrayList();
                userMessage.getGroups().put(groupId, groupMessages);
            }
            groupMessages.add(chatBody);
        });
        return userMessage;
    }

    /**
     * 组装放入用户好友消息;
     *
     * @param userMessage
     * @param messages
     */
    public UserMessageData putFriendsMessage(UserMessageData userMessage, List<ChatBody> messages, String friendId)
    {
        if (Objects.isNull(userMessage) || CollectionUtils.isEmpty(messages)) {
            return userMessage;
        }
        messages.forEach(chatBody -> {
            String fromId = chatBody.getFrom();
            if (StringUtils.isEmpty(fromId)) {
                return;
            }
            String targetFriendId = friendId;
            if (StringUtils.isEmpty(targetFriendId)) {
                targetFriendId = fromId;
            }
            List<ChatBody> friendMessages = userMessage.getFriends().get(targetFriendId);
            if (CollectionUtils.isEmpty(friendMessages)) {
                friendMessages = new ArrayList();
                userMessage.getFriends().put(targetFriendId, friendMessages);
            }
            friendMessages.add(chatBody);
        });
        return userMessage;
    }

    /**
     * 获取群组所有成员信息
     *
     * @param groupId                               群组ID
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    @Override
    public Group getGroupUsers(String groupId, Integer type)
    {
        if (Objects.isNull(groupId) || Objects.isNull(type)) {
            log.warn("group:{} or type:{} is null", groupId, type);
            return null;
        }
        Group group = XimRedisCacheManager.getCache(GROUP).get(groupId + SUFFIX + INFO, Group.class);
        if (Objects.isNull(group)) {
            return null;
        }
        List<String> userIds = this.getGroupUsers(groupId);
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        List<User> users = new ArrayList<User>();
        userIds.forEach(userId -> {
            User user = getUserByType(userId, type);
            if (Objects.isNull(user)) return;
            validateStatusByType(type, users, user);
        });
        group.setUsers(users);
        return group;
    }

    /**
     * 根据获取type校验是否组装User
     *
     * @param type
     * @param users
     * @param user
     */
    private void validateStatusByType(Integer type, List<User> users, User user)
    {
        String status = user.getStatus();
        if (UserStatusType.ONLINE.getNumber() == type && UserStatusType.ONLINE.getStatus().equals(status)) {
            users.add(user);
        } else if (UserStatusType.OFFLINE.getNumber() == type && UserStatusType.OFFLINE.getStatus().equals(status)) {
            users.add(user);
        } else if (UserStatusType.ALL.getNumber() == type) {
            users.add(user);
        }
    }

    @Override
    public User getUserByType(String userId, Integer type)
    {
        User user = XimRedisCacheManager.getCache(USER).get(userId + SUFFIX + INFO, User.class);
        if (Objects.isNull(user)) {
            return null;
        }
        boolean isOnline = this.isOnline(userId);
        String status = isOnline ? UserStatusType.ONLINE.getStatus() : UserStatusType.OFFLINE.getStatus();
        if (UserStatusType.ONLINE.getNumber() == type && isOnline) {
            user.setStatus(status);
            return user;
        } else if (UserStatusType.OFFLINE.getNumber() == type && !isOnline) {
            user.setStatus(status);
            return user;
        } else if (type == UserStatusType.ALL.getNumber()) {
            user.setStatus(status);
            return user;
        }
        return null;
    }

    @Override
    public Group getFriendUsers(String userId, String friendGroupId, Integer type)
    {
        boolean isTrue = Objects.isNull(userId) || Objects.isNull(friendGroupId) || Objects.isNull(type);
        if (isTrue) {
            log.warn("userId:{} or friendGroupId:{} or type:{} is null");
            return null;
        }
        List<Group> friends = XimRedisCacheManager.getCache(USER).get(userId + SUFFIX + FRIENDS, List.class);
        if (CollectionUtils.isEmpty(friends)) {
            return null;
        }
        for (Group group : friends) {
            if (!friendGroupId.equals(group.getGroupId())) continue;
            List<User> users = group.getUsers();
            if (CollectionUtils.isEmpty(users)) {
                return group;
            }
            List<User> userResults = new ArrayList<User>();
            for (User user : users) {
                initUserStatus(user);
                validateStatusByType(type, userResults, user);
            }
            group.setUsers(userResults);
            return group;
        }
        return null;
    }

    /**
     * 初始化用户在线状态;
     *
     * @param user
     */
    public boolean initUserStatus(User user)
    {
        if (Objects.isNull(user) || Objects.isNull(user.getUserId())) {
            return false;
        }
        String userId = user.getUserId();
        boolean isOnline = this.isOnline(userId);
        if (isOnline) {
            user.setStatus(UserStatusType.ONLINE.getStatus());
        } else {
            user.setStatus(UserStatusType.OFFLINE.getStatus());
        }
        return true;
    }

    /**
     * 获取好友分组所有成员信息
     *
     * @param userId                                用户ID
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    @Override
    public List<Group> getAllFriendUsers(String userId, Integer type)
    {
        if (Objects.isNull(userId)) {
            return null;
        }
        List<JSONObject> friendJsonArray = XimRedisCacheManager.getCache(USER).get(userId + SUFFIX + FRIENDS, List.class);
        if (CollectionUtils.isEmpty(friendJsonArray)) {
            return null;
        }
        List<Group> friends = new ArrayList<Group>();
        friendJsonArray.forEach(groupJson -> {
            Group group = JSONObject.toJavaObject(groupJson, Group.class);
            List<User> users = group.getUsers();
            if (CollectionUtils.isEmpty(users)) {
                return;
            }
            List<User> userResults = new ArrayList<User>();
            for (User user : users) {
                initUserStatus(user);
                validateStatusByType(type, userResults, user);
            }
            group.setUsers(userResults);
            friends.add(group);
        });
        return friends;
    }

    @Override
    public List<Group> getAllGroupUsers(String userId, Integer type)
    {
        if (Objects.isNull(userId)) {
            return null;
        }
        List<String> groupIds = XimRedisCacheManager.getCache(USER).listGetAll(userId + SUFFIX + GROUP);
        if (CollectionUtils.isEmpty(groupIds)) {
            return null;
        }
        List<Group> groups = new ArrayList<Group>();
        groupIds.forEach(groupId -> {
            Group group = getGroupUsers(groupId, type);
            if (Objects.isNull(group)) return;
            groups.add(group);
        });
        return groups;
    }

    /**
     * 更新用户终端协议类型及在线状态;
     *
     * @param user 用户信息
     */
    @Override
    public boolean updateUserTerminal(User user)
    {
        String userId = user.getUserId();
        String terminal = user.getTerminal();
        String status = user.getStatus();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(terminal) || StringUtils.isEmpty(status)) {
            log.error("userId:{},terminal:{},status:{} must not null", userId, terminal, status);
            return false;
        }
        if (UserStatusType.ONLINE.getStatus().equals(status)) {
            enhancedRedisService.sAdd(USER + SUFFIX + TERMINAL + SUFFIX + terminal, userId);
        } else if (UserStatusType.OFFLINE.getStatus().equals(status)) {
            enhancedRedisService.sDel(USER + SUFFIX + TERMINAL + SUFFIX + terminal, userId);
        }
//        XimRedisCacheManager.getCache(USER).put(userId+SUFFIX+TERMINAL+SUFFIX+terminal, user.getStatus());
        return true;
    }

    /**
     * 获取用户拥有的群组;
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getGroups(String userId)
    {
        List<String> groups = XimRedisCacheManager.getCache(USER).listGetAll(userId + SUFFIX + GROUP);
        return groups;
    }

    static {
        XimRedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
        XimRedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
        XimRedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        XimRedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
}
