package org.frameworkset.plugin.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.util.concurrent.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class BaseKafkaConsumerThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(BaseKafkaConsumerThread.class);

	protected StoreService storeService;
	protected String name;
	protected  boolean shutdown ;
	protected BaseKafkaConsumer consumer;
	private KafkaConsumer kafkaConsumer;
	protected  String[] topics;
	protected long pollTimeout;
	protected String keyDeserializer;
	private String groupId;
	protected String valueDeserializer;
	protected Integer maxPollRecords;
	protected Integer workThreads ;
	protected Integer workQueue = 100;
	protected ExecutorService executor;
	protected Boolean batch = true;
	protected int partition;
	protected String discardRejectMessage;
	public String getDiscardRejectMessage() {
		return discardRejectMessage;
	}

	public void setDiscardRejectMessage(String discardRejectMessage) {
		this.discardRejectMessage = discardRejectMessage;
	}
//	String topic,
//	private HDFSService logstashService;
//	protected ConsumerConnector consumer;
	public BaseKafkaConsumerThread(int partition,BaseKafkaConsumer consumer,String[] topics,StoreService storeService) {
		this.storeService = storeService;
		this.partition = partition;
		this.name = "KafkaBatchConsumer-"+topicsStr(topics)+"-p"+partition;
		this.consumer = consumer;
		this.topics = topics;

		BaseApplicationContext.addShutdownHook(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		});

	}
	private static String topicsStr(String[] topics){
		StringBuilder builder = new StringBuilder();
		boolean s = false;
		for(String topic:topics) {
			if(!s) {
				builder.append(topic);
				s = true;
			}
			else{
				builder.append(",").append(topic);
			}
		}
		return builder.toString();
	}
	public void setPollTimeout(long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}

	public long getPollTimeout() {
		return pollTimeout;
	}

	public void setWorkQueue(Integer workQueue) {
		this.workQueue = workQueue;
	}

	public void setWorkThreads(Integer workThreads) {
		this.workThreads = workThreads;
	}

	public Integer getWorkQueue() {
		return workQueue;
	}

	public Integer getWorkThreads() {
		return workThreads;
	}

	public void setBatch(Boolean batch) {
		this.batch = batch;
	}

	public Boolean getBatch() {
		return batch;
	}



	public void setMaxPollRecords(Integer maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	public Integer getMaxPollRecords() {
		return maxPollRecords;
	}



	public void setValueDeserializer(String valueDeserializer) {
		this.valueDeserializer = valueDeserializer;
	}

	public String getValueDeserializer() {
		return valueDeserializer;
	}

	public void setKeyDeserializer(String keyDeserializer) {
		this.keyDeserializer = keyDeserializer;
	}

	public String getKeyDeserializer() {
		return keyDeserializer;
	}

	public void shutdown(){
		if(shutdown)
			return;
		this.shutdown = true;
		if(executor != null){
			try {
				executor.shutdown();
			}
			catch (Exception e){
				logger.warn("",e);
			}
		}
		try {
			Thread.currentThread().interrupt();
		}
		catch (Exception e){
			logger.warn("",e);
		}
	}
	private void buildConsumerAndSubscribe(){
		Properties properties = consumer.getConsumerPropes();
		Properties threadProperties = new Properties();
		threadProperties.putAll(properties);
		if(keyDeserializer != null && !keyDeserializer.equals("")){
			threadProperties.put("key.deserializer",keyDeserializer);
		}
		if(valueDeserializer != null && !valueDeserializer.equals("")){
			threadProperties.put("value.deserializer",valueDeserializer);
		}
		if(maxPollRecords != null){
			threadProperties.put("max.poll.records",maxPollRecords+"");
		}
		if(groupId != null && !groupId.equals(""))
		{
			threadProperties.put("group.id",groupId);
		}
		if(workThreads != null){
			executor = ThreadPoolFactory.buildThreadPool(name,discardRejectMessage == null?"Kafka consumer message handle":discardRejectMessage,
					workThreads,workQueue,5000,1000,true,true);
		}
		kafkaConsumer = new KafkaConsumer(threadProperties);
		kafkaConsumer.subscribe(Arrays.asList(topics));
	}

	@Override
	public void run() {
		try {
			buildConsumerAndSubscribe();
//					Map<String, List<PartitionInfo>> listMap = consumer.listTopics();

			while (true) {
				if (shutdown)
					break;
				ConsumerRecords<Object, Object> records = kafkaConsumer.poll(pollTimeout);
				if(records != null && !records.isEmpty()){
					handleDatas( executor, kafkaConsumer, consumer, records);
				}
				/**
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


					} catch (InterruptedException e) {
						if (logger.isErrorEnabled())
							logger.error("涓柇寮傚父锛�", e);
						this.shutdown();
						break;
					} catch (ShutdownException e) {
						if (logger.isErrorEnabled())
							logger.error("涓柇寮傚父锛�", e);
						this.shutdown();
						break;
					} catch (Exception e) {
						if (logger.isErrorEnabled())
							logger.error("绯荤粺寮傚父锛�", e);
					} catch (Throwable e) {
						if (logger.isErrorEnabled())
							logger.error("绯荤粺寮傚父锛�", e);

					}
				}
				 */
			}
		}
		catch (Throwable e){
			if(logger.isErrorEnabled())
				logger.error("",e);
		}

	}
	private void doHandle(ConsumerRecords<Object, Object> records){
		try {
			if(batch != null && batch)
				storeService.store(records);
			else{
				for(ConsumerRecord consumerRecord:records) {
					storeService.store(consumerRecord);
				}
			}
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
	protected void handleDatas(ExecutorService executor, KafkaConsumer kafkaConsumer, BaseKafkaConsumer consumer, final ConsumerRecords<Object, Object> records){
		if(executor != null) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					doHandle( records);
				}
			});

		}
		else{
			doHandle( records);
		}

	}
//	protected abstract void handleData(BaseKafkaConsumer consumer,ConsumerRecord<Object, Object> record)  throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
