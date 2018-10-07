package com.demo.message;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 描述:单节点限制消费速率的消费者
 */
public abstract class QGMessageRateLimitConsumer implements QGMessageConsumer {

    private RateLimiter rateLimiter = RateLimiter.create(getRateLimit());

    /**
     * 限速处理消息
     *
     * @param message 消息， 是个字符串。用String.valueOf(message)转化
     */
    @Override
    public void handleMessage(Object message) {
        rateLimiter.acquire();
        onMessage(message);
    }

    /**
     * 消息处理
     *
     * @param message
     */
    public abstract void onMessage(Object message);

    /**
     * 限速 per-second
     *
     * @return
     */
    public abstract int getRateLimit();
}
