package com.able.disruptor.compare;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 13:54
 */
@Slf4j
public class ArrayBlockingQueue4Test {
    // 14:05:02.525 [Thread-1] INFO com.able.disruptor.compare.ArrayBlockingQueue4Test - ArrayBlockingQueue num=10000000 constTime= 2360
    // 14:06:16.256 [Thread-1] INFO com.able.disruptor.compare.ArrayBlockingQueue4Test - ArrayBlockingQueue num=10000000 constTime= 1887
//     14:07:35.004 [Thread-1] INFO com.able.disruptor.compare.ArrayBlockingQueue4Test - ArrayBlockingQueue num=10000000 constTime= 2111


//    14:52:03.886 [Thread-1] INFO com.able.disruptor.compare.ArrayBlockingQueue4Test - ArrayBlockingQueue num=100000000 constTime= 21560



    public static void main(String[] args){
        int num=Constants.EVENT_NUM_OHM;

        ArrayBlockingQueue<Data> queue =new ArrayBlockingQueue<>(num);
        long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long i=0;
                while (i < num){
                    Data data=new Data(i,"c"+i);
                    try {
                        queue.put(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long i=0;
                while (i < num){
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                long endTime = System.currentTimeMillis();
                log.info("ArrayBlockingQueue num={} constTime= {}",num,(endTime-startTime));
            }
        }).start();

    }
}

