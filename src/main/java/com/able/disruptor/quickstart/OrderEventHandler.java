package com.able.disruptor.quickstart;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 15:32
 */
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("消费者: {}",event.getValue());
    }
}

