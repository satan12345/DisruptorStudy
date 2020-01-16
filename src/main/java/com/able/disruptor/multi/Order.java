package com.able.disruptor.multi;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Order {
    private String id;
    private String name;
    private Double price;
}
