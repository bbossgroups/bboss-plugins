package org.frameworkset.plugin.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.support.ApplicationObjectSupport;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaBatchConsumer extends BaseKafkaConsumer {
 
	/**
	 * 批量处理数据大小
	 */
	private int batchsize = 1000;
	/**
	 * 定时检查器，如果批量处理队列中的记录数不为0并且在指定的时间内没有到达batchsize对应的数据量，
	 * 则强制进行处理并清空
	 */
	private long checkinterval = 3000l;
	private int worker = 10;
 

//	String topic,String zookeeperConnect, HDFSService logstashService
	
	public KafkaBatchConsumer() {

	}
	 
	@Override
	protected Runnable buildRunnable(KafkaStream<byte[], byte[]> stream) {
		// TODO Auto-generated method stub
		if(this.batchsize > 0)
			return new KafkaBatchConsumerThread(stream,storeService,this.batchsize,this.checkinterval,worker);
		else
			return new KafkaConsumerThread(stream,storeService);
	}


}
