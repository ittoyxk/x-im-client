package net.commchina.ximbiz.remote;

import net.commchina.framework.common.response.APIResponse;
import org.jim.core.packets.ChatBody;
import org.jim.core.packets.Group;
import org.jim.core.packets.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 17:44
 */
@FeignClient(name = "im-core")
public interface ImCoreRemote {


    /**
     * 获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    @GetMapping("/job/v1/msg/getOfflineMessage")
    APIResponse<List<ChatBody>> getOfflineMessage(@RequestParam("key") String key);

    /**
     * 模糊获取与所有用户离线消息;
     *
     * @param key 持久化ID
     * @return
     */
    @GetMapping("/job/v1/msg/fuzzyOfflineMessage")
    APIResponse<List<ChatBody>> fuzzyOfflineMessage(@RequestParam("key") String key);

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @return
     */
    @GetMapping("/job/v1/msg/getHistoryMessage")
    APIResponse<List<ChatBody>> getHistoryMessage(@RequestParam("key") String key);

    /**
     * 获取与指定用户历史消息;
     *
     * @param key 持久化ID
     * @param min 消息区间开始时间
     * @param max 消息区间结束时间
     * @return
     */
    @GetMapping("/job/v1/msg/getHistoryMessageByTime")
    APIResponse<List<ChatBody>> getHistoryMessage(@RequestParam("key") String key, @RequestParam("min") double min, @RequestParam("max") double max);

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
    @GetMapping("/job/v1/msg/getHistoryMessageByTimePage")
    APIResponse<List<ChatBody>> getHistoryMessage(@RequestParam("key") String key, @RequestParam("min") double min, @RequestParam("max") double max, @RequestParam("offset") int offset, @RequestParam("count") int count);

    /**
     * 查询当前用户所在群组
     *
     * @param userId
     * @return
     */
    @GetMapping("/job/v1/user/getGroups")
    APIResponse<List<Group>> getGroups(@RequestParam("userId") String userId);

    /**
     * 查询用户详情
     *
     * @param userId
     * @return
     */
    @GetMapping("/job/v1/user/getUserInfo")
    APIResponse<User> getUserInfo(@RequestParam("userId") String userId);

    /**
     * 查询好友
     *
     * @param userId
     * @return
     */
    @GetMapping("/job/v1/user/initFriends")
    APIResponse<Group> initFriends(@RequestParam("userId") String userId);

    @GetMapping("/job/v1/msg/find/{msgId}")
    APIResponse<ChatBody> isRecall(@PathVariable("msgId")  String msgId);
}
