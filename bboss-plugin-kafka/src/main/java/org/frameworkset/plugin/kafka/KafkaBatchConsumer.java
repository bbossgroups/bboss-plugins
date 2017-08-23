package org.frameworkset.plugin.kafka;

import kafka.consumer.KafkaStream;

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
	/**
	 * lastreceive:最后一次接收的时间为基准
	 * lastsend:最后一次发送的时间为基准
	 */
	private String checkmode = "lastsend";
 

//	String topic,String zookeeperConnect, HDFSService logstashService
	
	public KafkaBatchConsumer() {

	}
	 
	@Override
	protected Runnable buildRunnable(KafkaStream<byte[], byte[]> stream) {
		// TODO Auto-generated method stub
		if(this.batchsize > 0)
			return new KafkaBatchConsumerThread(stream,storeService,this.batchsize,this.checkinterval,checkmode,worker);
		else
			return new KafkaConsumerThread(stream,storeService);
	}


}
