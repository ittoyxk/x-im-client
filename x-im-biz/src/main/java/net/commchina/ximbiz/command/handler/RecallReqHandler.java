package net.commchina.ximbiz.command.handler;

import net.commchina.ximbiz.processor.RecallCmdProcessor;
import net.commchina.ximbiz.command.packets.RecallBody;
import org.jim.core.ImChannelContext;
import org.jim.core.ImPacket;
import org.jim.core.ImStatus;
import org.jim.core.config.ImConfig;
import org.jim.core.exception.ImException;
import org.jim.core.packets.ChatType;
import org.jim.core.packets.Command;
import org.jim.core.packets.RespBody;
import org.jim.core.utils.JsonKit;
import org.jim.server.ImServerChannelContext;
import org.jim.server.JimServerAPI;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.config.ImServerConfig;
import org.jim.server.protocol.ProtocolManager;
import org.jim.server.queue.MsgQueueRunnable;
import org.jim.server.util.ChatKit;

import java.util.Objects;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/12/9 16:50
 */
public class RecallReqHandler extends AbstractCmdHandler {

    public RecallReqHandler()
    {
    }

    @Override
    public Command command()
    {
        return Command.COMMAND_CANCEL_MSG_REQ;
    }

    @Override
    public ImPacket handler(ImPacket packet, ImChannelContext channelContext) throws ImException
    {
        ImServerChannelContext imServerChannelContext = (ImServerChannelContext)channelContext;
        if (packet.getBody() == null) {
            throw new ImException("body is null");
        } else {
            RecallBody chatBody = JsonKit.toBean(packet.getBody(), RecallBody.class);
            packet.setBody(chatBody.toByte());
            if (chatBody != null && ChatType.forNumber(chatBody.getChatType()) != null) {
                //校验消息是否可撤回
                RecallCmdProcessor recallCmdProcessor = this.getSingleProcessor(RecallCmdProcessor.class);
                boolean recall = recallCmdProcessor.isRecall(chatBody.getId());
                if(!recall){
                    RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CANCEL_MSG_RESP);
                    chatDataInCorrectRespPacket.setCode(10022).setMsg("消息撤回失败");
                    ImPacket respPacket = ProtocolManager.Converter.respPacket(chatDataInCorrectRespPacket, channelContext);
                    respPacket.setStatus(ImStatus.C10001);
                    return respPacket;
                }
                MsgQueueRunnable msgQueueRunnable = this.getMsgQueueRunnable(imServerChannelContext);
                msgQueueRunnable.addMsg(chatBody);
                msgQueueRunnable.executor.execute(msgQueueRunnable);
                ImPacket chatPacket = new ImPacket(Command.COMMAND_CANCEL_MSG_REQ, (new RespBody(Command.COMMAND_CANCEL_MSG_REQ, chatBody)).toByte());
                chatPacket.setSynSeq(packet.getSynSeq());
                ImServerConfig imServerConfig = (ImServerConfig) ImConfig.Global.get();
                boolean isStore = ImServerConfig.ON.equals(imServerConfig.getIsStore());
                String groupId;
                if (ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()) {
                    groupId = chatBody.getTo();
                    if (ChatKit.isOnline(groupId, isStore)) {
                        JimServerAPI.sendToUser(groupId, chatPacket);

                        RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CANCEL_MSG_RESP, ImStatus.C10000);
                        ImPacket respPacket = ProtocolManager.Converter.respPacket(chatDataInCorrectRespPacket, channelContext);
                        respPacket.setStatus(ImStatus.C10000);
                        return respPacket;
                    } else {

                        RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CANCEL_MSG_RESP, ImStatus.C10001);
                        ImPacket respPacket = ProtocolManager.Converter.respPacket(chatDataInCorrectRespPacket, channelContext);
                        respPacket.setStatus(ImStatus.C10001);
                        return respPacket;
                    }
                } else if (ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()) {
                    groupId = chatBody.getGroupId();
                    JimServerAPI.sendToGroup(groupId, chatPacket);

                    RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CANCEL_MSG_RESP, ImStatus.C10000);
                    ImPacket respPacket = ProtocolManager.Converter.respPacket(chatDataInCorrectRespPacket, channelContext);
                    respPacket.setStatus(ImStatus.C10000);
                    return respPacket;
                } else {
                    return null;
                }
            } else {
                RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP, ImStatus.C10002);
                ImPacket respPacket = ProtocolManager.Converter.respPacket(chatDataInCorrectRespPacket, channelContext);
                respPacket.setStatus(ImStatus.C10002);
                return respPacket;
            }
        }
    }

    private MsgQueueRunnable getMsgQueueRunnable(ImServerChannelContext imServerChannelContext) {
        MsgQueueRunnable msgQueueRunnable = (MsgQueueRunnable)imServerChannelContext.getMsgQue();
        if (Objects.nonNull(msgQueueRunnable.getProtocolCmdProcessor())) {
            return msgQueueRunnable;
        } else {
            Class var3 = MsgQueueRunnable.class;
            synchronized(MsgQueueRunnable.class) {
                msgQueueRunnable.setProtocolCmdProcessor(this.getSingleProcessor());
                return msgQueueRunnable;
            }
        }
    }
}
