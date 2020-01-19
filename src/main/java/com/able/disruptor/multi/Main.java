package com.able.disruptor.multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @param
 * @author jipeng
 * @date 2020-01-16 15:11
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventFactory<Order> eventFactory = new EventFactory<Order>() {
            @Override
            public Order newInstance() {
                return new Order();
            }
        };
        int ringBufferSize=1024*1024;
        //1 创建ringbuffer
        RingBuffer<Order> orderRingBuffer = RingBuffer.create(ProducerType.MULTI, eventFactory, ringBufferSize,
                new YieldingWaitStrategy());
        //通过ringBuffer创建一个屏障
        SequenceBarrier sequenceBarrier = orderRingBuffer.newBarrier();

        //3 创建多个消费者数组
        Consumer[] consumers=new Consumer[10];
        for (int i = 0; i < 10; i++) {
            consumers[i]=new Consumer("C"+i);
        }


        //4 构建多消费者工作池
        WorkerPool workerPool=new WorkerPool(orderRingBuffer,
                sequenceBarrier,
                new OrderExceptionHandler(),
                consumers);

        // 5 设置多个消费者的sequence序号 用于单独统计消费进度 并且设置到ringBuffer中
       orderRingBuffer.addGatingSequences(workerPool.getWorkerSequences());

       //6 启动workerPool
       workerPool.start(Executors.newFixedThreadPool(20));

        CountDownLatch countDownLatch=new CountDownLatch(1);

        for (int i = 0; i < 20; i++) {
            Producer producer=new Producer();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                       countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    for (int i1 = 0; i1 < 1; i1++) {
                        producer.sendData(orderRingBuffer);
//                    }

                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(2);

        countDownLatch.countDown();

        TimeUnit.SECONDS.sleep(5);
        log.info("消费者处理的任务总数为: {}",Consumer.counter.get());

    }
        @Slf4j
        static  class OrderExceptionHandler implements ExceptionHandler{

            @Override
            public void handleEventException(Throwable ex, long sequence, Object event) {
                log.error("handleEventException",ex);
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                log.error("handleOnStartException",ex);
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                log.error("handleOnShutdownException ",ex);
            }
    }
}

