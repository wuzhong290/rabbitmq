package com.demo.message;

/**
 * 延迟消息的消费者.
 *
 *  所有的延迟消费者需要实现这个接口，并且加入到Spring的context中
 */
public interface QGDelayMessageConsumer {

    /**
     * 希望延迟多少分钟处理这个消息
     * @return 延迟多长时间
     */
    int getDelayInMinutes();


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
