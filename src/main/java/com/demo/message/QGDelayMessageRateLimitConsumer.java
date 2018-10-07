package com.demo.message;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 描述:
 */
public abstract class QGDelayMessageRateLimitConsumer implements QGDelayMessageConsumer {

    private RateLimiter rateLimiter = RateLimiter.create(getRateLimit());

    /**
     * 处理消息
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
