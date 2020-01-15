package com.able.disruptor.quickstart;

import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 16:44
 */
@Slf4j
public class OrderEventProducer {
    RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }


    public void sendData(ByteBuffer buffer) {
        //1 生产者在发生消息的时候 首先需要我们的 ringBuffer里面获取下一个可用的序号
        long seq = ringBuffer.next();
        try {
            //2 根据这个需要 找到具体的Event 元素 注意：此时获取的orderEvent对象 是一个属性没有被赋值的对象
            OrderEvent orderEvent = ringBuffer.get(seq);

            //log.info("获得orderEvent={}", orderEvent);
            orderEvent.setValue(buffer.getLong());
        } finally {
            //提交发布操作
            ringBuffer.publish(seq);
        }


    }
}

