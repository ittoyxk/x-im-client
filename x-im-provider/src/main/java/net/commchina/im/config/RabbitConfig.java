package net.commchina.im.config;

import net.commchina.xim.common.constant.AppConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/11/16 18:00
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange topicExchange()
    {
        return new TopicExchange(AppConstant.IM_CHAT_MESSAGE_EXCHANGE, true, false);
    }

    @Bean
    public Binding sendBinding(TopicExchange topicExchange, Queue queue)
    {
        return BindingBuilder.bind(queue).to(topicExchange).with(AppConstant.IM_CHAT_MESSAGE_ROUTING_KEY);
    }

    @Bean
    public Queue queue()
    {
        return new Queue(AppConstant.IM_CHAT_MESSAGE_QUEUE, true);
    }
}
