package com.able.disruptor.quickstart;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 16:04
 */
@Slf4j
public class Main {
    public static void main(String[] args){

        OrderEventFactory orderEventFactory=new OrderEventFactory();
        int ringBufferSize=4;

        /**EventFactory :消息(event)工厂对象
         * ringBufferSize:容器的长度
         * ThreadFactory :线程工厂
         * ProducerType : 生产者类型（单生产者还是多生产者）
         * WaitStrategy : 等待策略
         *
         */
        //1 创建disruptor
        Disruptor<OrderEvent> disruptor=new Disruptor(orderEventFactory,
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
        //2 添加消费者监听
        EventHandler<OrderEvent> eventEventHandler=new OrderEventHandler();
        disruptor.handleEventsWith(eventEventHandler);

        //启动disruptor
        disruptor.start();

        //获取实际存储数据的容器 ringBuffer
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();


        OrderEventProducer orderEventProducer=new OrderEventProducer(ringBuffer);
        ByteBuffer buffer=ByteBuffer.allocate(8);
        for (int i = 0; i < 5; i++) {
            buffer.putLong(i);
            buffer.flip();
            orderEventProducer.sendData(buffer);
            buffer.clear();

        }


        disruptor.shutdown();

//        for (int i = 0; i < 10; i++) {
//            long seq = ringBuffer.next();
//            OrderEvent orderEvent = ringBuffer.get(seq);
//            log.info("获得orderEvent={}",orderEvent);
//            orderEvent.setValue(i);
//            ringBuffer.publish(seq);
//        }


    }
}

