package com.able.disruptor.high;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Main {
    public static void main(String[] argsl) throws Exception {
        final ExecutorService es = Executors.newFixedThreadPool(4);
         AtomicInteger count=new AtomicInteger(0);
        EventFactory eventFactory = new EventFactory() {
            @Override
            public Object newInstance() {
                Trade trade = new Trade();
                count.incrementAndGet();
                return trade;
            }
        };

        int ringBufferSize = 1024 * 1024;
        //1 构建Disruptor
        Disruptor<Trade> disruptor = new Disruptor<Trade>(eventFactory,
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
        log.info("count={}",count.get());

        //2 把消费者设置到disruptor中handleEventWith
        /**
         * 11:01:17.110 [pool-1-thread-1] INFO com.able.disruptor.high.TradeEventTranslator - sequence= 0
         * 11:01:17.115 [pool-2-thread-1] INFO com.able.disruptor.high.Handler1 - handler1 SET NAME
         * 11:01:18.115 [pool-2-thread-2] INFO com.able.disruptor.high.Handler2 - handler2 SET ID
         * 11:01:20.116 [pool-2-thread-3] INFO com.able.disruptor.high.Handler3 - handler3 : event=Trade(id=0c39fa26-83d0-49be-b0f2-2871d793be63, name=旗木卡卡西, price=0.6320572694176543, count=0)
         * 11:01:20.119 [main] INFO com.able.disruptor.high.Main - 总耗时:3103
         */
        //2.1 串行操作 1-->2-->3
//        disruptor.handleEventsWith(new Handler1())
//                .handleEventsWith(new Handler2())
//                .handleEventsWith(new Handler3());


        /**
         * 2.2 并行操作
         * 11:03:01.409 [pool-1-thread-1] INFO com.able.disruptor.high.TradeEventTranslator - sequence= 0
         * 11:03:01.414 [pool-2-thread-1] INFO com.able.disruptor.high.Handler1 - handler1 SET NAME
         * 11:03:01.415 [pool-2-thread-3] INFO com.able.disruptor.high.Handler3 - handler3 : event=Trade(id=null, name=旗木卡卡西, price=0.4838846242848004, count=0)
         * 11:03:01.416 [pool-2-thread-2] INFO com.able.disruptor.high.Handler2 - handler2 SET ID
         * 11:03:03.418 [main] INFO com.able.disruptor.high.Main - 总耗时:2048
         */
//        disruptor.handleEventsWith(new Handler1());
//        disruptor.handleEventsWith(new Handler2());
//        disruptor.handleEventsWith(new Handler3());


        /**
         * 11:12:55.237 [pool-2-thread-3] INFO com.able.disruptor.high.Handler3 - handler3 : event=Trade(id=null, name=null, price=null, count=0)
         * 11:12:56.237 [pool-2-thread-1] INFO com.able.disruptor.high.Handler1 - handler1 SET NAME
         * 11:12:57.237 [pool-2-thread-2] INFO com.able.disruptor.high.Handler2 - handler2 SET ID
         */
        disruptor.handleEventsWith(new Handler1(),new Handler2(),new Handler3());
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
@Slf4j
class Handler1 implements EventHandler<Trade>, WorkHandler<Trade>{

    //EventHandler
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        //log.info("handler1 eventHandler ");
        onEvent(event);
    }
    //WorkHandler
    @Override
    public void onEvent(Trade event) throws Exception {
        //log.info("handler1 WorkHandler ");
        TimeUnit.SECONDS.sleep(1);
        log.info("handler1 SET NAME");
        event.setName("旗木卡卡西");

    }
}
@Slf4j
class Handler2 implements EventHandler<Trade>{
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        log.info("handler2 SET ID");
        event.setId(UUID.randomUUID().toString());
    }
}
@Slf4j
class Handler3 implements EventHandler<Trade>{
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
            log.info("handler3 : event={}",event);
    }
}
@Slf4j
class Handler4 implements EventHandler<Trade>{
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("handler4 SET PIRCE");
        event.setPrice(17.0);
    }
}
@Slf4j
class Handler5 implements EventHandler<Trade>{
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("handler5 GET PRICE:{}",event.getPrice());
        event.setPrice(event.getPrice()+3.0);

    }
}
