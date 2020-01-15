package com.able.disruptor.high;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class TradePublisher implements Runnable {
    CountDownLatch countDownLatch;
    Disruptor<Trade> disruptor;
    int publishCount = 10;

    public TradePublisher(CountDownLatch countDownLatch, Disruptor<Trade> disruptor) {
        this.countDownLatch = countDownLatch;
        this.disruptor = disruptor;
    }

    public void run() {
        final TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i < publishCount; i++) {
            //新的提交任务的方式
            disruptor.publishEvent(tradeEventTranslator);

        }
        countDownLatch.countDown();
    }


}

@Slf4j
class TradeEventTranslator implements EventTranslator<Trade> {

    Random random = new Random();

    public void translateTo(Trade event, long sequence) {
        log.info("sequence= {}", sequence);
        this.generateTrade(event);

    }

    private void generateTrade(Trade event) {
        event.setPrice(random.nextDouble());
    }
}
