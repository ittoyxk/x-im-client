package net.commchina.ximbiz.processor;

import org.jim.core.ImChannelContext;
import org.jim.core.packets.Message;
import org.jim.server.processor.SingleProtocolCmdProcessor;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/12/10 9:30
 */
public abstract class RecallCmdProcessor implements SingleProtocolCmdProcessor {

    /**
     * 校验消息是否可撤回
     * @param msgId
     * @return
     */
    public abstract boolean isRecall(String msgId);

    @Override
    public void process(ImChannelContext imChannelContext, Message message)
    {

    }
}
