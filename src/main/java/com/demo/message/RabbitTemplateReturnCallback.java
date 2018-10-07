package com.demo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * rabbit:template return-callback="returnCallback" mandatory="true" 同时设置才生效
 */
public class RabbitTemplateReturnCallback implements RabbitTemplate.ReturnCallback{

    private final Logger logger = LoggerFactory.getLogger(RabbitTemplateReturnCallback.class);
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.info("消息主体 message : "+message);
        logger.info("消息主体 message : "+replyCode);
        logger.info("描述："+replyText);
        logger.info("消息使用的交换器 exchange : "+exchange);
        logger.info("消息使用的路由键 routing : "+routingKey);
    }
}
