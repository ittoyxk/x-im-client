package net.commchina.ximbiz.command.packets;

import com.alibaba.fastjson.JSONObject;
import org.jim.core.packets.Message;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/12/9 16:53
 */
public class RecallBody extends Message {

    private String from;
    private String to;
    private Integer chatType;
    private String groupId;

    private RecallBody()
    {
    }

    private RecallBody(String id, String from, String to, Integer chatType, String groupId, Integer cmd, Long createTime, JSONObject extras)
    {
        this.id = id;
        this.from = from;
        this.to = to;
        this.chatType = chatType;
        this.groupId = groupId;
        this.cmd = cmd;
        this.createTime = createTime;
        this.extras = extras;
    }

    public static RecallBody.Builder newBuilder()
    {
        return new RecallBody.Builder();
    }

    public String getFrom()
    {
        return this.from;
    }

    public RecallBody setFrom(String from)
    {
        this.from = from;
        return this;
    }

    public String getTo()
    {
        return this.to;
    }

    public RecallBody setTo(String to)
    {
        this.to = to;
        return this;
    }


    public String getGroupId()
    {
        return this.groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public Integer getChatType()
    {
        return this.chatType;
    }

    public RecallBody setChatType(Integer chatType)
    {
        this.chatType = chatType;
        return this;
    }

    public static class Builder extends org.jim.core.packets.Message.Builder<RecallBody, RecallBody.Builder> {
        private String from;
        private String to;
        private Integer chatType;
        private String groupId;

        public Builder()
        {
        }

        public RecallBody.Builder from(String from)
        {
            this.from = from;
            return this;
        }

        public RecallBody.Builder to(String to)
        {
            this.to = to;
            return this;
        }


        public RecallBody.Builder chatType(Integer chatType)
        {
            this.chatType = chatType;
            return this;
        }


        public RecallBody.Builder groupId(String groupId)
        {
            this.groupId = groupId;
            return this;
        }

        @Override
        protected RecallBody.Builder getThis()
        {
            return this;
        }

        @Override
        public RecallBody build()
        {
            return new RecallBody(this.id, this.from, this.to, this.chatType, this.groupId, this.cmd, this.createTime, this.extras);
        }
    }
}
