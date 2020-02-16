package org.frameworkset.plugin.kafka;


import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.frameworkset.spi.BaseApplicationContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KafkaBatchConsumerThread extends BaseKafkaConsumerThread{

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
	/**
	 * 并行消费处理消息
	 */
	private boolean parallel = false;
	private String discardRejectMessage ;
	private BlockingQueue<List<MessageAndMetadata<byte[], byte[]>>> queue;
//	private boolean lastReceive = false;
//	private ThreadPoolExecutor executor = null;

	private BatchCheckor batchCheckor;
//	private HandleWork handleWork;
	private String topic;


	public KafkaBatchConsumerThread(BaseKafkaConsumer consumer,KafkaStream<byte[], byte[]> stream, StoreService storeService,
									int batchsize , long checkinterval, String topic, boolean parallel , String discardRejectMessage) {
		super(consumer,  "KafkaBatchConsumerThread-"+topic,topic,stream,  storeService);
		this.discardRejectMessage = discardRejectMessage;
		this.parallel = parallel;
		this.topic = topic;
//		this.lastReceive = checkmode != null && checkmode.equals("lastreceive")?true:false;
		this.batchsize = batchsize;
		if(checkinterval > 0l){
			this.checkinterval = checkinterval;
		}

		messageQueue = new LinkedList<>();
		batchCheckor = new BatchCheckor();


//		batchCheckor.setDaemon(true);

		batchCheckor.start();
//		this.workQueue = workerQueue;
//		queue = new java.util.concurrent.LinkedBlockingQueue(workQueue);
//		handleWork = new HandleWork();
//		handleWork.start();
//		if(this.parallel) {
//			int poolsize = worker > 0 ? worker : 10;
//			executor = new ThreadPoolExecutor(poolsize, poolsize,
//					0L, TimeUnit.MILLISECONDS,
//					new LinkedBlockingQueue<Runnable>(workQueue), Executors.defaultThreadFactory());//Executors.newFixedThreadPool(worker > 0?worker:10);
//
//		}

		BaseApplicationContext.addShutdownHook(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		});
		if(logger.isDebugEnabled()){
			StringBuilder builder = new StringBuilder();
			builder.append("KafkaBatchConsumerThread:batchsize=").append(batchsize).append(",checkinterval=").append(checkinterval)
					.append("ms parallel=").append(this.parallel);
			logger.debug(builder.toString());
		}


	}

	public void shutdown(){
		if(batchCheckor != null)
			batchCheckor.shutdown();
//		if(executor != null){
//			executor.shutdown();
//		}
//		if(handleWork != null){
//			handleWork.shutdown();
//		}
		super.shutdown();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	class BatchCheckor extends Thread{
		private boolean shutdown;
		public void shutdown(){
			if(shutdown)
				return;
			shutdown = true;
			this.interrupt();
		}
		@Override
		public void run() {
			while(true){
				if(this.shutdown)
					break;
				try {
					synchronized(this){
						wait(checkinterval);
					}
				} catch (InterruptedException e) {
					logger.debug("",e);
					break;
				}
				if(this.shutdown)
					break;
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

	private void _storeData(List<MessageAndMetadata<byte[], byte[]>> data){
		lastSendedTime = System.currentTimeMillis();
		if(data != null && data.size() > 0){
			try {
				storeService.store(data);
			}
			catch (ShutdownException e){
				throw e;
			}
			catch (Exception e) {
				logger.error("", e);
			} catch (Throwable e) {
				logger.error("", e);
			}
		}
	}
	private void handleRejectedExecutionException(RejectedExecutionException ree) {
		final int error = rejectedExecutionCount.incrementAndGet();
		final int mod = 100;
		if ((error % mod) == 0) {
			logger.warn("KafkaBatchConsumerThread RejectedExecutionCount={}", error);
		}
	}
	private final AtomicInteger rejectedExecutionCount = new AtomicInteger(0);
	/**
	class HandleWork extends Thread{
		private boolean shutdown;
		public void shutdown(){
			if(shutdown)
				return;
			shutdown = true;
			this.interrupt();
		}
		public void run(){
			while(true){

				try {
					if(shutdown)
						break;
					final List<MessageAndMetadata<byte[], byte[]>> data = queue.poll(10, TimeUnit.SECONDS);
					if(data != null && data.size() > 0) {
						if(parallel) {

							executor(data);
						}
						else {
							_storeData(data);
						}

					}
				} catch (InterruptedException e) {
					break;
				}



			}
		}
	}

*/
	private boolean idleToLimit(){
		long step = (System.currentTimeMillis() - lastReceiveTime) ;
		return (step > this.checkinterval) && (messageQueue.size() > 0) ;
	}

	protected void handleData(BaseKafkaConsumer consumer,MessageAndMetadata<byte[], byte[]> mam)  throws Exception{
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
/**
	private void executor(final List<MessageAndMetadata<byte[], byte[]>> data){
		long interval = 500l;
		do {
			try {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						_storeData(data);
					}
				});
				break;
			} catch (RejectedExecutionException e) {
				handleRejectedExecutionException(e);
				if(!discardRejectMessage) {
					try {
						Thread.sleep(interval);//睡眠500毫秒，继续提交作业
					} catch (InterruptedException e1) {
						break;
					}
					if (interval < 10000l) {//每次重试，递增等待时间，最大等待时间10秒
						interval = interval + 100l;
					}
				}
				else{
					break;
				}

			}
		}while(true);
	}*/
	private void handleDatas()   {

		boolean needSend = idleToLimit() ;
		boolean touchSize = messageQueue.size() >= this.batchsize;
		if(touchSize || needSend){//第一次检查
			List<MessageAndMetadata<byte[], byte[]>> data = new ArrayList<MessageAndMetadata<byte[], byte[]>>(messageQueue);
			messageQueue.clear();
			_storeData(data);

//			if(parallel) {
//				List<MessageAndMetadata<byte[], byte[]>> data = new ArrayList<MessageAndMetadata<byte[], byte[]>>(messageQueue);
//				messageQueue.clear();
//				executor(data);
//			}
//			else {
//				try {
//					List<MessageAndMetadata<byte[], byte[]>> data = new ArrayList<MessageAndMetadata<byte[], byte[]>>(messageQueue);
//					messageQueue.clear();
//					this.queue.put(data);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}
}