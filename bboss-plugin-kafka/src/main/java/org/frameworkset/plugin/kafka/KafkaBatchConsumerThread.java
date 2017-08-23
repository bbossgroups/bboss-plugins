package org.frameworkset.plugin.kafka;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class KafkaBatchConsumerThread extends BaseKafkaConsumerThread{
	private static final Logger logger = LoggerFactory.getLogger(KafkaBatchConsumerThread.class);
 
	private List<MessageAndMetadata<byte[], byte[]>> messageQueue ;
	private Lock lock = new ReentrantLock();
	/**
	 * 批量处理数据大小
	 */
	private int batchsize = 1000;
	/**
	 * 定时检查器，如果批量处理队列中的记录数不为0并且在指定的时间内没有到达batchsize对应的数据量，
	 * 则强制进行处理并清空
	 */
	private long checkinterval = 5000l;
	private long lastSendTime = 0l;
	private boolean lastReceive = false;
	private ExecutorService executor = null;
 
	private Thread batchCheckor;
	public KafkaBatchConsumerThread(KafkaStream<byte[], byte[]> stream,StoreService storeService,int batchsize ,long checkinterval,String checkmode,int worker) {
		super(stream,  storeService);
		this.lastReceive = checkmode != null && checkmode.equals("lastreceive")?true:false;
		this.batchsize = batchsize;
		if(checkinterval > 0l){
			this.checkinterval = checkinterval; 
		}
		
		messageQueue = new LinkedList<>();
		batchCheckor = new Thread(new BatchCheckor());
		batchCheckor.start();
		executor = Executors.newFixedThreadPool(worker > 0?worker:10);
		if(logger.isDebugEnabled()){
			StringBuilder builder = new StringBuilder();
			builder.append("KafkaBatchConsumerThread:batchsize=").append(batchsize).append(",checkinterval=").append(checkinterval)
			.append("ms,worker=").append(worker > 0?worker:10);
			logger.debug(builder.toString());
		}
		
		
	}
	class BatchCheckor implements Runnable{

		@Override
		public void run() {
			while(true){
				try {
					synchronized(this){
						wait(checkinterval);
					}
				} catch (InterruptedException e) {
					logger.debug("",e);
					break;
				}
				logger.debug("Batch idle time check after {} 毫秒",checkinterval);
				handleDatas();				
			}
			
		}
		
	}
	 
	
	private boolean idleToLimit(){
		long step = (System.currentTimeMillis() - lastSendTime) ;		
		return step > this.checkinterval && messageQueue.size() > 0 ;
	}
	
	protected   void handleData(MessageAndMetadata<byte[], byte[]> mam)  throws Exception{
		try{
			if(lastReceive){
				lastSendTime = System.currentTimeMillis();
			}
			else if(this.lastSendTime == 0l )
				lastSendTime = System.currentTimeMillis();
			lock.lock();
			messageQueue.add(mam);
			//logger.debug("message comming.");
		}
		finally{
			lock.unlock();
		}
		handleDatas();
			
		
		
	}
	
	private void handleDatas(){
		List<MessageAndMetadata<byte[], byte[]>> data = null;
		boolean needSend = idleToLimit() ;
		boolean touchSize = messageQueue.size() >= this.batchsize;
		//logger.debug("touchSize：" + touchSize+",needSend:"+needSend);
		if(touchSize || needSend){//第一次检查
			try
			{				
				lock.lock();				
				needSend = idleToLimit() ;
				touchSize = messageQueue.size() >= this.batchsize;
				//logger.debug("touchSize：" + touchSize+",needSend:"+needSend);
				if(touchSize || needSend){//第一次检查
					data = new ArrayList<>(messageQueue);			
					messageQueue.clear();
					if(!lastReceive)
						lastSendTime = System.currentTimeMillis();
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
		if(data != null && data.size() > 0){
			final List<MessageAndMetadata<byte[], byte[]>> data_ = data;
			executor.submit(new Callable<Void>(){
				@Override
				public Void call() throws Exception {					
					storeService.store(data_);
					return null;
				}
				
			});
			
		}
		
		
	}
}
