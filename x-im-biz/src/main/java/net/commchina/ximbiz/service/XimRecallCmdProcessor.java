package net.commchina.ximbiz.service;

import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.util.SpringContextUtil;
import net.commchina.ximbiz.processor.RecallCmdProcessor;
import net.commchina.ximbiz.remote.ImCoreRemote;
import org.jim.core.packets.ChatBody;

import java.time.Instant;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/12/10 9:38
 */
@Slf4j
public class XimRecallCmdProcessor extends RecallCmdProcessor {

    private ImCoreRemote imCoreRemote;

    public XimRecallCmdProcessor()
    {
        this.imCoreRemote = SpringContextUtil.getBean(ImCoreRemote.class);
    }

    /**
     * 校验消息是否可撤回
     *
     * @param msgId
     * @return
     */
    @Override
    public boolean isRecall(String msgId)
    {
        try {
            ChatBody data = imCoreRemote.isRecall(msgId).getData();
            Long createTime = data.getCreateTime();
            long now = Instant.now().toEpochMilli();
            return now-createTime<=120000;
        } catch (Exception e) {
            log.info("消息撤回校验失败:{}",e);
            return false;
        }
    }
}
