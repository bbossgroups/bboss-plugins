package org.frameworkset.plugin.kafka;

import kafka.consumer.KafkaStream;

public class KafkaBatchConsumer extends BaseKafkaConsumer {
 
	/**
	 * 批量处理数据大小
	 */
	protected int batchsize = 1000;
	/**
	 * 定时检查器，如果批量处理队列中的记录数不为0并且在指定的时间内没有到达batchsize对应的数据量，
	 * 则强制进行处理并清空
	 */
	protected long checkinterval = 3000l;
	/**
	 * 并行消费处理消息
	 */
	protected boolean parallel = false;
	protected String discardRejectMessage  ;
	/**
	 * lastreceive:最后一次接收的时间为基准
	 * lastsend:最后一次发送的时间为基准
	 */
//	protected String checkmode = "lastsend";
 

//	String topic,String zookeeperConnect, HDFSService logstashService
	
	public KafkaBatchConsumer() {

	}

	public void setBatchsize(int batchsize) {
		this.batchsize = batchsize;
	}

	public void setCheckinterval(long checkinterval) {
		this.checkinterval = checkinterval;
	}

	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	public void setDiscardRejectMessage(String discardRejectMessage) {
		this.discardRejectMessage = discardRejectMessage;
	}



	@Override
	protected Runnable buildRunnable(KafkaStream<byte[], byte[]> stream, String topic) {
		// TODO Auto-generated method stub
		if(this.batchsize > 0)
			return new KafkaBatchConsumerThread(this,stream,storeService,this.batchsize,
					this.checkinterval,   topic,parallel,discardRejectMessage);
		else
			return new KafkaConsumerThread(this,stream,storeService,  topic);
	}


}
