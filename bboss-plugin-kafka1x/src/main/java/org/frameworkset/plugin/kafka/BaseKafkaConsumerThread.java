package org.frameworkset.plugin.kafka;


import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseKafkaConsumerThread implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected KafkaStream<byte[], byte[]> stream;
	 
	protected StoreService storeService;
	protected String name;
	protected  boolean shutdown ;
	protected BaseKafkaConsumer consumer;
	protected  String topic;
//	String topic,
//	private HDFSService logstashService;
//	protected ConsumerConnector consumer;
	public BaseKafkaConsumerThread(BaseKafkaConsumer consumer,String topic,String name,KafkaStream<byte[], byte[]> stream,StoreService storeService) {
		this.stream = stream;
		this.storeService = storeService;
		this.name = name;
		this.consumer = consumer;
		this.name = name;
		this.topic = topic;

		
	}
	public void shutdown(){
		if(shutdown)
			return;
		this.shutdown = true;
		Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();

		//logger.debug(Thread.currentThread().getName() + ": dddddddddddddddddddddddddd");

		while (it.hasNext()) {
			if(shutdown)
				break;
			MessageAndMetadata<byte[], byte[]> mam = it.next();
			try {
				if(storeService != null){
					handleData( consumer,mam) ;
				}
				else
				{
					logger.debug(Thread.currentThread().getName() + ": partition[" + mam.partition() + "]," 
							+ "offset[" + mam.offset() + "], " + new String(mam.message(),"UTF-8"));
				}
//				Map<TopicAndPartition, OffsetAndMetadata> var1 = new HashMap<TopicAndPartition, OffsetAndMetadata>();
//				TopicAndPartition topicAndPartition = new TopicAndPartition(this.topic,mam.partition());
//				OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(0l);
//				var1.put(topicAndPartition,null);
//				this.consumer.commitOffset();

			}
			catch (InterruptedException e){
				logger.error("中断异常：",e);
				this.shutdown();
				break;
			}
			catch (ShutdownException e){
				logger.error("中断异常：",e);
				this.shutdown();
				break;
			}
			catch (Exception e) {
				logger.error("系统异常：",e);
			}
			catch (Throwable e) {
				logger.error("系统异常：",e);

			}

		}
	}
	protected abstract void handleData(BaseKafkaConsumer consumer,MessageAndMetadata<byte[], byte[]> mam)  throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
