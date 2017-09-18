package org.frameworkset.plugin.kafka;


import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private long lastSendedTime = 0l;
	private long lastReceiveTime = 0l;
	private boolean lastReceive = false;
	private ThreadPoolExecutor executor = null;

	private Thread batchCheckor;
	public KafkaBatchConsumerThread(KafkaStream<byte[], byte[]> stream,StoreService storeService,int batchsize ,long checkinterval,String checkmode,int worker,String topic) {
		super("KafkaBatchConsumerThread-"+topic,stream,  storeService);
		this.lastReceive = checkmode != null && checkmode.equals("lastreceive")?true:false;
		this.batchsize = batchsize;
		if(checkinterval > 0l){
			this.checkinterval = checkinterval;
		}

		messageQueue = new LinkedList<>();
		batchCheckor = new Thread(new BatchCheckor());
		batchCheckor.start();
		executor = new ThreadPoolExecutor(worker > 0?worker:10, worker > 0?worker:10,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(10),Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				throw new RejectedExecutionException();
			}
		});//Executors.newFixedThreadPool(worker > 0?worker:10);
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
				try {
					lock.lock();
					handleDatas();
				}
				finally {
					lock.unlock();
				}
			}

		}

	}


	private boolean idleToLimit(){
		long step = (System.currentTimeMillis() - lastReceiveTime) ;
		return (step > this.checkinterval) && (messageQueue.size() > 0) ;
	}

	protected   void handleData(MessageAndMetadata<byte[], byte[]> mam)  throws Exception{
		try{
			lastReceiveTime = System.currentTimeMillis();

			lock.lock();

			messageQueue.add(mam);
			if(logger.isDebugEnabled())
				logger.debug("Thread["+this.getName() +"] handleData messageQueue size:"+messageQueue.size());
			if(touchBatchsize()){
				handleDatas();
			}
			//logger.debug("message comming.");
		}
		finally{
			lock.unlock();
		}




	}
	private boolean touchBatchsize(){
		boolean touchSize = (messageQueue.size() >= this.batchsize);
		return touchSize;
	}

	private void handleDatas(){

		boolean needSend = idleToLimit() ;
		boolean touchSize = messageQueue.size() >= this.batchsize;
		//logger.debug("touchSize：" + touchSize+",needSend:"+needSend);
		if(touchSize || needSend){//第一次检查
			//logger.debug("touchSize：" + touchSize+",needSend:"+needSend);
			List<MessageAndMetadata<byte[], byte[]>> data = new ArrayList<>(messageQueue);
			messageQueue.clear();
			this.lastSendedTime = System.currentTimeMillis();
			if(data != null && data.size() > 0){
				try {
					storeService.store(data);
//							Thread.currentThread().sleep(100);
				} catch (Exception e) {
					logger.error("", e);
				} catch (Throwable e) {
					logger.error("", e);
				}
//				Runnable task = new Runnable() {
//					public void run() {
//						try {
//							storeService.store(data);
////							Thread.currentThread().sleep(100);
//						} catch (Exception e) {
//							logger.error("", e);
//						} catch (Throwable e) {
//							logger.error("", e);
//						}
//
//					}
//
//				};
//				do {
//					try {
//						executor.execute(task);
//						try {
//							Thread.currentThread().sleep(100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						break;
//					} catch (RejectedExecutionException e) {
//						try {
//							Thread.currentThread().sleep(500);
//							continue;
//						} catch (InterruptedException e1) {
//
//							break;
//						}
//					}
//				}while (true);

			}

		}




	}
}