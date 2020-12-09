package net.commchina.ximbiz.command.packets;

import com.alibaba.fastjson.JSONObject;
import org.jim.core.packets.Message;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/12/9 16:53
 */
public class ReadStatusBody extends Message {

    private String from;
    private String to;
    private Integer chatType;
    private String groupId;

    private ReadStatusBody()
    {
    }

    private ReadStatusBody(String id, String from, String to, Integer chatType, String groupId, Integer cmd, Long createTime, JSONObject extras)
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

    public static ReadStatusBody.Builder newBuilder()
    {
        return new ReadStatusBody.Builder();
    }

    public String getFrom()
    {
        return this.from;
    }

    public ReadStatusBody setFrom(String from)
    {
        this.from = from;
        return this;
    }

    public String getTo()
    {
        return this.to;
    }

    public ReadStatusBody setTo(String to)
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

    public ReadStatusBody setChatType(Integer chatType)
    {
        this.chatType = chatType;
        return this;
    }

    public static class Builder extends Message.Builder<ReadStatusBody, ReadStatusBody.Builder> {
        private String from;
        private String to;
        private Integer chatType;
        private String groupId;

        public Builder()
        {
        }

        public ReadStatusBody.Builder from(String from)
        {
            this.from = from;
            return this;
        }

        public ReadStatusBody.Builder to(String to)
        {
            this.to = to;
            return this;
        }


        public ReadStatusBody.Builder chatType(Integer chatType)
        {
            this.chatType = chatType;
            return this;
        }


        public ReadStatusBody.Builder groupId(String groupId)
        {
            this.groupId = groupId;
            return this;
        }

        @Override
        protected ReadStatusBody.Builder getThis()
        {
            return this;
        }

        @Override
        public ReadStatusBody build()
        {
            return new ReadStatusBody(this.id, this.from, this.to, this.chatType, this.groupId, this.cmd, this.createTime, this.extras);
        }
    }
}
