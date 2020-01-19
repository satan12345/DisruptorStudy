package com.able.disruptor.multi;

import com.lmax.disruptor.RingBuffer;

import java.util.Random;
import java.util.UUID;

/**
 * @param
 * @author jipeng
 * @date 2020-01-16 17:52
 */
public class Producer {
    public void sendData(RingBuffer<Order> orderRingBuffer) {
        long seq = orderRingBuffer.next();
        try {
            Order order = orderRingBuffer.get(seq);
            order.setId(String.valueOf(new Random().nextInt(100)));
        } finally {

            orderRingBuffer.publish(seq);
        }

    }
}

