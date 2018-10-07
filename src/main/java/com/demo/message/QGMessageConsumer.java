package com.demo.message;

/**
 * 消息的消费者.
 *
 *  所有的消费者需要实现这个接口，并且加入到Spring的context中
 */
public interface QGMessageConsumer {

    /**
     * 消费者要处理的消息的Topic。
     * @return  消费者要处理的消息的Topic.
     */
     String getMessageTopic();


    /**
     * 处理消息
     * @param message 消息， 是个字符串。用String.valueOf(message)转化
     */
    void handleMessage(Object message);


}
