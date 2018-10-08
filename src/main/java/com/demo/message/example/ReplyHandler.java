package com.demo.message.example;

import com.demo.message.QGMessageConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class ReplyHandler implements ChannelAwareMessageListener, QGMessageConsumer {
    private final Logger logger = LoggerFactory.getLogger(ReplyHandler.class);
    @Override
    public String getMessageTopic() {
        return "rpc_test";
    }

    @Override
    public void handleMessage(Object message) {
        logger.info("消费确认Reply："+message.toString());
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("消费手动确认Reply："+message.toString());
        logger.info("消费手动确认Reply deliveryTag："+message.getMessageProperties().getDeliveryTag());
        String id = new String(message.getMessageProperties().getCorrelationId());
        logger.info("消费手动确认Reply correlationIdString："+id);
        logger.info("消费手动确认Reply replyTo："+message.getMessageProperties().getReplyTo());
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(id)
                .build();
       String[] replyTo = message.getMessageProperties().getReplyTo().split("/");

        channel.basicPublish(replyTo[0], replyTo[1], replyProps, "111111111111111111".getBytes("UTF-8"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
