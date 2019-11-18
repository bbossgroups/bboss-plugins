package org.frameworkset.plugin.kafka;

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

	protected boolean discardRejectMessage = false ;

	
	public KafkaBatchConsumer() {

	}
	 
	@Override
	protected Runnable buildRunnable( String[] topic) {
		// TODO Auto-generated method stub
		if(this.batchsize > 0)
			return new KafkaBatchConsumerThread(this,topic,storeService,this.batchsize,
					this.checkinterval,pollTimeOut,discardRejectMessage);
		else
			return new KafkaConsumerThread(this,topic,storeService,  pollTimeOut);
	}

	public void setBatchsize(int batchsize) {
		this.batchsize = batchsize;
	}

	public void setDiscardRejectMessage(boolean discardRejectMessage) {
		this.discardRejectMessage = discardRejectMessage;
	}

	public void setCheckinterval(long checkinterval) {
		this.checkinterval = checkinterval;
	}
}
