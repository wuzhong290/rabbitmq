package com.demo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * rabbit:connection-factory  publisher-confirms="true"才有效
 * rabbit:template confirm-callback="confirmCallback"
 *
 * 只确认是否正确到达 Exchange 中,至于是否有队列不关心，是否到达队列也不关心
 */
public class RabbitTemplateConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private final Logger logger = LoggerFactory.getLogger(RabbitTemplateConfirmCallback.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info("消息唯一标识："+correlationData);
        logger.info("确认结果："+ack);
        logger.info("失败原因："+cause);
    }
}
