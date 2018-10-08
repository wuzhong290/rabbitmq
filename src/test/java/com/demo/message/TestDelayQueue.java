package com.demo.message;

import com.demo.message.model.OrderRepairEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-bean-test.xml","classpath*:applicationContext-rabbitmq.xml"})
public class TestDelayQueue {
    @Resource
    private QGProducerTemplate producerTemplate;

    /**
     * OrderRepairEventHandler消息接受应答模式为：AcknowledgeMode#AUTO
     * OrderRepairEventHandler消费者对应的工厂为：DelayConsumerFactory
     * 请求队列：qg:default:order_repair，其消费者：OrderRepairEventHandler
     * 对应的消费者为：OrderRepairEventHandler
     * 延迟队列为：delay:10m.queue
     * 队列：qg_delay:default:order_repair
     */
    @Test
    public void testDelayQueue(){
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
}
