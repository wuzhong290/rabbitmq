package com.demo.message.example;

import com.demo.message.QGMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoticeHandler implements QGMessageConsumer {
    private final Logger logger = LoggerFactory.getLogger(NoticeHandler.class);
    @Override
    public String getMessageTopic() {
        return "notice";
    }

    @Override
    public void handleMessage(Object message) {
        logger.info(message.toString());
    }
}
