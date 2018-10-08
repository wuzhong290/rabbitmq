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
public class TestRPC {
    @Resource
    private RabbitTemplate amqpTemplate;

    /**
     * ReplyHandler消息接受应答模式为：AcknowledgeMode.MANUAL
     * ReplyHandler消费者对应的工厂为：DefaultConsumerFactory
     * 请求队列：qg:default:rpc_test，其消费者：ReplyHandler
     * 响应队列：qg:default:reply,消费者：amqpTemplate
     */
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
        Object reply = amqpTemplate.convertSendAndReceive("amq.topic", "rpc_test", orderRepairEvent, null, correlationData);
        System.out.println(new String((byte[])reply));
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
