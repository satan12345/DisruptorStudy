package com.able.disruptor.compare;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

/**
 * @param
 * @author jipeng
 * @date 2020-01-15 14:17
 */
public class DisruptorSingle4Test {
//    14:45:48.812 [pool-1-thread-1] INFO com.able.disruptor.compare.DataConsumenr - Disruptor num=10000000 constime =1176
//    14:49:39.021 [pool-1-thread-1] INFO com.able.disruptor.compare.DataConsumenr - Disruptor num=10000000 constime =1066
//    14:50:01.517 [pool-1-thread-1] INFO com.able.disruptor.compare.DataConsumenr - Disruptor num=10000000 constime =1198

//    14:52:52.086 [pool-1-thread-1] INFO com.able.disruptor.compare.DataConsumenr - Disruptor num=100000000 constime =9086
//    14:58:41.839 [Thread-0] INFO com.able.disruptor.compare.DataConsumenr - Disruptor num=100000000 constime =7999

    public static void main(String[] args){
      int ringBufferSize=1024*1024;
      int num=Constants.EVENT_NUM_OHM;
        Disruptor<Data> disruptor=new Disruptor<>(new EventFactory<Data>() {
            @Override
            public Data newInstance() {
                return new Data(1L, "默认值");
            }
        }, ringBufferSize,Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        //消费数据
        disruptor.handleEventsWith(new DataConsumenr(num));
        disruptor.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RingBuffer<Data> ringBuffer=disruptor.getRingBuffer();
                for (int i = 0; i < num; i++) {
                    long seq = ringBuffer.next();
                    Data data=ringBuffer.get(seq);
                    data.setI(i);
                    data.setName("c"+i);
                    ringBuffer.publish(seq);
                }
            }
        }).start();
    }
}
@Slf4j
class DataConsumenr implements EventHandler<Data>{

    private long startTime;
    private int i;
    private long num;
    public DataConsumenr(long num){
       startTime=System.currentTimeMillis();
       this.num=num;
    }
    @Override
    public void onEvent(Data data, long l, boolean b) throws Exception {
        i++;
        if (i==num){
            long endTime=System.currentTimeMillis();
            log.info("Disruptor num={} constime ={}",num,endTime-startTime);
        }
    }
}

