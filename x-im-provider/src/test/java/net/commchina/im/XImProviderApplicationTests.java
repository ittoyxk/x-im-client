package net.commchina.im;

import net.commchina.framework.boot.redis.EnhancedRedisService;
import net.commchina.xim.common.util.BeanUtils;
import net.commchina.ximbiz.config.XimRedisCacheManager;
import org.apache.commons.collections4.CollectionUtils;
import org.jim.core.packets.ChatBody;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
class XImProviderApplicationTests {

    @Autowired
    private EnhancedRedisService enhancedRedisService;
    @Test
    void contextLoads()
    {
        Set<String> userKeys = enhancedRedisService.keys("push" + ":" + "user" + ":" + "88"+"*");
        //获取好友离线消息;
        if (CollectionUtils.isNotEmpty(userKeys)) {
            List<ChatBody> messageList = new ArrayList<ChatBody>();
            Iterator<String> userKeyIterator = userKeys.iterator();
            while (userKeyIterator.hasNext()) {
                String userKey = userKeyIterator.next();
                userKey = userKey.substring(userKey.indexOf("push" + ":")+5);
                List<String> messages = XimRedisCacheManager.getCache("push").sortSetGetAll(userKey);
                XimRedisCacheManager.getCache("push").remove(userKey);
                messageList.addAll(BeanUtils.toArray(messages, ChatBody.class));
            }
        }
    }

}
