package com.demo.message;

import com.demo.message.model.OrderRepairEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-bean-test.xml","classpath*:applicationContext-rabbitmq.xml"})
public class TestManualAcks {
    @Resource
    private QGProducerTemplate producerTemplate;

    /**
     * NoticeHandlerACK消息接受应答模式为：AcknowledgeMode.MANUAL
     * NoticeHandlerACK消费者对应的工厂为：DefaultConsumerFactory
     * 请求队列：qg:default:notice，其消费者：NoticeHandlerACK
     */
    @Test
    public void testMQ(){
        OrderRepairEvent orderRepairEvent = new OrderRepairEvent();
        orderRepairEvent.setUid(1);
        orderRepairEvent.setUdid("udid13");
        orderRepairEvent.setOrderCode(1);
        orderRepairEvent.setRepairAddress(true);
        orderRepairEvent.setAddressId("addressId");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(System.currentTimeMillis() + "");
        producerTemplate.send("notice", orderRepairEvent, null,correlationData);
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
