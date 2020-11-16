package net.commchina.ximbiz.service;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.commchina.ximbiz.processor.LoginServiceProcessor;
import org.jim.core.packets.Group;
import org.jim.core.packets.User;
import org.jim.core.session.id.impl.UUIDSessionIdGenerator;

import java.util.*;

/**
 * @author xiaokang
 *
 */
@Slf4j
public class XimLoginServiceProcessor extends LoginServiceProcessor {


    public static final Map<String, User> tokenMap = new HashMap<>();

    private static String[] familyName = new String[]{"J-", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
            "吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱"};

    private static String[] secondName = new String[]{"艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
            "明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
            "大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
            "子家", "德利优视", "五方会谈", "来电话了", "轨迹", "超"};


    /**
     * 查询当前用户所在群组
     *
     * @param userId
     * @return
     */
    @Override
    public List<Group> getGroups(String userId)
    {
        return Arrays.asList(Group.newBuilder().groupId("100").name("X-IM朋友圈").build());
    }

    /**
     * 查询用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public User getUserInfo(String userId)
    {
        return User.newBuilder().avatar(nextImg()).build();
    }

    /**
     * 查询好友
     * @param userId
     * @return
     */
    @Override
    public Group initFriends(String userId)
    {
        Group myFriend = Group.newBuilder().groupId("1").name("我的好友").build();
        List<User> myFriendGroupUsers = new ArrayList();
        User user1 = User.newBuilder()
                .userId(UUIDSessionIdGenerator.instance.sessionId(null))
                .nick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)])
                .avatar(nextImg())
                .build();
        myFriendGroupUsers.add(user1);
        myFriend.setUsers(myFriendGroupUsers);
        return myFriend;
    }

    public String nextImg()
    {
        return ImgMnService.nextImg();
    }

}
