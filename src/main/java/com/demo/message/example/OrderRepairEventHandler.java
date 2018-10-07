package com.demo.message.example;

import com.demo.message.QGDelayMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderRepairEventHandler implements QGDelayMessageConsumer {
    private final Logger logger = LoggerFactory.getLogger(OrderRepairEventHandler.class);
    @Override
    public int getDelayInMinutes() {
        return 10;
    }

    @Override
    public String getMessageTopic() {
        return "order_repair";
    }

    @Override
    public void handleMessage(Object message) {
        logger.info(message.toString());
    }
}
