package net.commchina.ximbiz.service;

import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.common.util.SpringContextUtil;
import net.commchina.ximbiz.processor.RecallCmdProcessor;
import net.commchina.ximbiz.remote.ImCoreRemote;

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
        return imCoreRemote.isRecall(msgId).getData();
    }
}
