package com.able.disruptor.high;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Trade {
    private String id;
    private String name;
    private Double price;
    private AtomicInteger count=new AtomicInteger(0);
}
