package com.demo.message.example;

import com.demo.message.QGMessageConsumer;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class NoticeHandlerACK implements ChannelAwareMessageListener, QGMessageConsumer {
    private final Logger logger = LoggerFactory.getLogger(NoticeHandlerACK.class);
    @Override
    public String getMessageTopic() {
        return "notice";
    }

    @Override
    public void handleMessage(Object message) {
        logger.info("消费确认："+message.toString());
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("消费手动确认："+message.toString());
        logger.info("消费手动确认deliveryTag："+message.getMessageProperties().getDeliveryTag());
        //1、确认消息
        //multiple：当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
        //false时，仅确认提供的delivery_tag的消息
        // channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        //2、否认消息
        //multiple 当该参数为 true 时，则可以一次性Nack delivery_tag 小于等于传入值的所有消息
        //false时，仅Nack提供的delivery_tag的消息
        //requeue 当该参数为 true 时，如果被Nack的消息应该被重新请求，而不是丢弃/死字符
        // channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        //3、拒绝消息
        //requeue 当该参数为 true 时，如果被拒绝的消息应该被重新请求，而不是丢弃/死字符
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
    }
}
