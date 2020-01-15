package com.able.disruptor.quickstart;

import com.lmax.disruptor.EventFactory;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 15:28
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setValue(-1);
        return orderEvent;
    }
}

