package net.commchina.ximbiz.command.packets;

import lombok.Data;
import org.jim.core.packets.Message;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2021/1/19 17:41
 */

@Data
public class UnBindGroupBody extends Message {

    private String groupId;
    private String userId;
}
