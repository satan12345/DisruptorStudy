package com.able.disruptor.multi;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @param
 * @author jipeng
 * @date 2020-01-16 15:24
 */
@Slf4j
public class Consumer implements WorkHandler<Order> {
    private String consumerId;
    static AtomicLong counter = new AtomicLong(0);
    Random random = new Random();

    public Consumer(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onEvent(Order event) throws Exception {
        TimeUnit.MICROSECONDS.sleep(random.nextInt(10));
        counter.incrementAndGet();
        log.info("当前消费者为:{} 消费信息id为:{}", consumerId, event.getId());
    }

    private long getCount() {
        return counter.get();
    }
}

