package net.commchina.ximbiz.command.handler;

import com.google.common.base.Strings;
import net.commchina.ximbiz.command.packets.UnBindGroupBody;
import org.jim.core.ImChannelContext;
import org.jim.core.ImPacket;
import org.jim.core.exception.ImException;
import org.jim.core.packets.Command;
import org.jim.core.utils.JsonKit;
import org.jim.server.JimServerAPI;
import org.jim.server.command.AbstractCmdHandler;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2021/1/19 17:35
 */
public class UnBindGroupReqHandler extends AbstractCmdHandler {

    public UnBindGroupReqHandler()
    {
        Command.addAndGet("COMMAND_UN_BIND_GROUP_REQ", 23);
    }

    @Override
    public Command command()
    {
        return Command.forNumber(23);
    }

    @Override
    public ImPacket handler(ImPacket packet, ImChannelContext imChannelContext) throws ImException
    {
        if (packet.getBody() == null) {
            throw new ImException("body is null");
        } else {
            UnBindGroupBody chatBody = JsonKit.toBean(packet.getBody(), UnBindGroupBody.class);
            String groupId = chatBody.getGroupId();
            String userId = chatBody.getUserId();
            if(Strings.isNullOrEmpty(groupId)){
                throw new ImException("body is null");
            }
            JimServerAPI.unbindGroup(groupId,imChannelContext);
        }
        return null;
    }
}
