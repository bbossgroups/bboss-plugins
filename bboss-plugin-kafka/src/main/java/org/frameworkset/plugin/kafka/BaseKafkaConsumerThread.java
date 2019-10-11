package org.frameworkset.plugin.kafka;



import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class BaseKafkaConsumerThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(BaseKafkaConsumerThread.class);

	protected StoreService storeService;
	protected String name;
	protected  boolean shutdown ;
	protected BaseKafkaConsumer consumer;
	private KafkaConsumer kafkaConsumer;
	protected  String[] topics;
	protected long timeOut;
//	String topic,
//	private HDFSService logstashService;
//	protected ConsumerConnector consumer;
	public BaseKafkaConsumerThread(BaseKafkaConsumer consumer,String[] topics,String name ,long timeOut,StoreService storeService) {
		this.storeService = storeService;
		this.name = name;
		this.consumer = consumer;
		this.name = name;
		this.topics = topics;
		this.timeOut = timeOut;

		
	}
	public void shutdown(){
		if(shutdown)
			return;
		this.shutdown = true;
		Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		try {
			kafkaConsumer = new KafkaConsumer(consumer.getConsumerPropes());
			kafkaConsumer.subscribe(Arrays.asList(topics));
//					Map<String, List<PartitionInfo>> listMap = consumer.listTopics();

			while (true) {
				if (shutdown)
					break;
				ConsumerRecords<Object, Object> records = kafkaConsumer.poll(timeOut);
				for (ConsumerRecord<Object, Object> record : records) {
					if (logger.isDebugEnabled())
						logger.debug("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
					try {
						if (storeService != null) {
							handleData(consumer, record);
						} else {
							if (logger.isDebugEnabled())
								logger.debug(Thread.currentThread().getName() + ": partition[" + record.partition() + "],"
										+ "offset[" + record.offset() + "], " + record.value());
						}
//				Map<TopicAndPartition, OffsetAndMetadata> var1 = new HashMap<TopicAndPartition, OffsetAndMetadata>();
//				TopicAndPartition topicAndPartition = new TopicAndPartition(this.topic,mam.partition());
//				OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(0l);
//				var1.put(topicAndPartition,null);
//				this.consumer.commitOffset();

					} catch (InterruptedException e) {
						if (logger.isErrorEnabled())
							logger.error("中断异常：", e);
						this.shutdown();
						break;
					} catch (ShutdownException e) {
						if (logger.isErrorEnabled())
							logger.error("中断异常：", e);
						this.shutdown();
						break;
					} catch (Exception e) {
						if (logger.isErrorEnabled())
							logger.error("系统异常：", e);
					} catch (Throwable e) {
						if (logger.isErrorEnabled())
							logger.error("系统异常：", e);

					}
				}
			}
		}
		catch (Throwable e){
			if(logger.isErrorEnabled())
				logger.error("",e);
		}
//		ConsumerIterator<byte[], byte[]> it = stream.iterator();
//
//		//logger.debug(Thread.currentThread().getName() + ": dddddddddddddddddddddddddd");
//
//		while (it.hasNext()) {
//			if(shutdown)
//				break;
//			MessageAndMetadata<byte[], byte[]> mam = it.next();
//			try {
//				if(storeService != null){
//					handleData( consumer,mam) ;
//				}
//				else
//				{
//					logger.debug(Thread.currentThread().getName() + ": partition[" + mam.partition() + "],"
//							+ "offset[" + mam.offset() + "], " + new String(mam.message(),"UTF-8"));
//				}
////				Map<TopicAndPartition, OffsetAndMetadata> var1 = new HashMap<TopicAndPartition, OffsetAndMetadata>();
////				TopicAndPartition topicAndPartition = new TopicAndPartition(this.topic,mam.partition());
////				OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(0l);
////				var1.put(topicAndPartition,null);
////				this.consumer.commitOffset();
//
//			}
//			catch (InterruptedException e){
//				logger.error("中断异常：",e);
//				this.shutdown();
//				break;
//			}
//			catch (ShutdownException e){
//				logger.error("中断异常：",e);
//				this.shutdown();
//				break;
//			}
//			catch (Exception e) {
//				logger.error("系统异常：",e);
//			}
//			catch (Throwable e) {
//				logger.error("系统异常：",e);
//
//			}
//
//		}
	}
	protected abstract void handleData(BaseKafkaConsumer consumer,ConsumerRecord<Object, Object> record)  throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
