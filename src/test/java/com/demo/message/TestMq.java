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
public class TestMq {
    @Resource
    private QGProducerTemplate producerTemplate;
    @Resource
    private RabbitTemplate amqpTemplate;
    @Test
    public void testDelayMQ(){
        OrderRepairEvent orderRepairEvent = new OrderRepairEvent();
        orderRepairEvent.setUid(1);
        orderRepairEvent.setUdid("udid14");
        orderRepairEvent.setOrderCode(1);
        orderRepairEvent.setRepairAddress(true);
        orderRepairEvent.setAddressId("addressId");
        producerTemplate.send("order_repair", orderRepairEvent, null, 10);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testReturnCallback(){
        OrderRepairEvent orderRepairEvent = new OrderRepairEvent();
        orderRepairEvent.setUid(1);
        orderRepairEvent.setUdid("return_callback_1");
        orderRepairEvent.setOrderCode(1);
        orderRepairEvent.setRepairAddress(true);
        orderRepairEvent.setAddressId("return_callback");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(System.currentTimeMillis() + "");
        producerTemplate.send("return_callback", orderRepairEvent, null,correlationData);
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
    @Test
    public void testRPC(){
        OrderRepairEvent orderRepairEvent = new OrderRepairEvent();
        orderRepairEvent.setUid(1);
        orderRepairEvent.setUdid("udid13");
        orderRepairEvent.setOrderCode(1);
        orderRepairEvent.setRepairAddress(true);
        orderRepairEvent.setAddressId("addressId");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(System.currentTimeMillis() + "");
        Object reply = amqpTemplate.convertSendAndReceive("amq.topic", "notice_test", orderRepairEvent, null, correlationData);
        System.out.println(new String((byte[])reply));
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
