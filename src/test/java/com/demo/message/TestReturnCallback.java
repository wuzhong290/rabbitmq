package com.demo.message;

import com.demo.message.model.OrderRepairEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-bean-test.xml","classpath*:applicationContext-rabbitmq.xml"})
public class TestReturnCallback {
    @Resource
    private QGProducerTemplate producerTemplate;

    /**
     * 队列return_callback不存在，也没有对应的消费者
     */
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
}
