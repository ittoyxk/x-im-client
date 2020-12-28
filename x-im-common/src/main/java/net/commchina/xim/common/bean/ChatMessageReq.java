package net.commchina.xim.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jim.core.packets.ChatBody;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 17:51
 */
@Data
@Builder
@Accessors(chain = true)
public class ChatMessageReq {

    /**
     * 1:   消息持久化写入
     * 2:   删除消息持久化
     * 3:   模糊删除消息持久化
     */
    private int msgType;

    private String key;

    private double score;

    private ChatBody chatBody;

}
