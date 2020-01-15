package com.able.disruptor.high;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Main {
    public static void main(String[] argsl) throws Exception {
        final ExecutorService es = Executors.newFixedThreadPool(4);

        EventFactory eventFactory = new EventFactory() {
            public Object newInstance() {
                return new Trade();
            }
        };
        int ringBufferSize = 1024 * 1024;
        //1 构建Disruptor
        Disruptor<Trade> disruptor = new Disruptor<Trade>(eventFactory,
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());

        //2 把消费者设置到disruptor中handleEventWith

        //3 启动disruptor
        final RingBuffer<Trade> ringBuffer = disruptor.start();
        CountDownLatch countDownLatch=new CountDownLatch(1);
        final long begin = System.currentTimeMillis();
        es.submit(new TradePublisher(countDownLatch,disruptor));

        countDownLatch.await();

        disruptor.shutdown();
        es.shutdown();

        log.info("总耗时:{}",System.currentTimeMillis()-begin);
    }
}

class Handler1 implements EventHandler<Trade>{

    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {

    }
}
class Handler2 implements EventHandler<Trade>{

    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {

    }
}
class Handler3 implements EventHandler<Trade>{

    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {

    }
}
class Handler4 implements EventHandler<Trade>{

    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {

    }
}
class Handler5 implements EventHandler<Trade>{

    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {

    }
}
