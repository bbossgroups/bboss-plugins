package org.frameworkset.plugin.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.WakeupException;
import org.frameworkset.util.concurrent.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BaseKafkaConsumerThread extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(BaseKafkaConsumerThread.class);

	protected StoreService storeService;
	protected String workThreadname;
	protected  boolean shutdown ;
	protected BaseKafkaConsumer consumer;
    protected boolean autoCommit = true;
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



	public long getBlockedWaitTimeout() {
		return blockedWaitTimeout;
	}

	public void setBlockedWaitTimeout(long blockedWaitTimeout) {
		this.blockedWaitTimeout = blockedWaitTimeout;
	}

	protected long blockedWaitTimeout = -1l;

	public int getWarnMultsRejects() {
		return warnMultsRejects;
	}

	public void setWarnMultsRejects(int warnMultsRejects) {
		this.warnMultsRejects = warnMultsRejects;
	}

	protected int warnMultsRejects = 500;
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
		super("KafkaBatchConsumer-"+topicsStr(topics)+"-p"+partition);
		workThreadname = this.getName() + "-work";
		this.storeService = storeService;
		this.partition = partition;
		this.consumer = consumer;
		this.topics = topics;


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
	private boolean consumerClosed;
	private void closeConsumer(){
		synchronized (this){
			if(consumerClosed)
				return;
			consumerClosed = true;
		}
		try {
			if (this.kafkaConsumer != null) {
				kafkaConsumer.close();
				logger.info("kafkaConsumer[{}] closed topic:{}",workThreadname,topics);
			}
		}
		catch (Exception e){
//			consumerClosed = false;
			logger.warn("kafkaConsumer["+workThreadname+"] closed topic:"+topics,e);
		}

	}

	public void shutdown(){
		synchronized (this) {
			if (shutdown)
				return;
			this.shutdown = true;

		}

        try {
            if(kafkaConsumer != null)
                kafkaConsumer.wakeup();
        }
        catch (Exception e){
            logger.warn("wakeup kafkaConsumer failed:",e);
        }

//            closeConsumer();
        try {
            this.join();

		}
		catch (Exception e){

		}
		if(executor != null){
			try {
                ThreadPoolFactory.shutdownExecutor(executor);
			}
			catch (Exception e){
				logger.warn("",e);
			}
		}

	}
	private synchronized void buildConsumerAndSubscribe(){
        if(shutdown)
            return;
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
			executor = ThreadPoolFactory.buildThreadPool(workThreadname,discardRejectMessage == null?"Kafka consumer message handle":discardRejectMessage,
					workThreads,workQueue,blockedWaitTimeout,warnMultsRejects,true,false);
		}

        this.autoCommit = consumer.isAutoCommit();
		kafkaConsumer = new KafkaConsumer(threadProperties);
		kafkaConsumer.subscribe(Arrays.asList(topics));
	}

	@Override
	public void run() {
		try {
			buildConsumerAndSubscribe();
//					Map<String, List<PartitionInfo>> listMap = consumer.listTopics();

			while (true) {
				if (shutdown) {
					closeConsumer();
					break;
				}
				try {
					ConsumerRecords<Object, Object> records = kafkaConsumer.poll(pollTimeout);                  
                    Future future = null;
					if(records != null && !records.isEmpty()){
                        future = handleDatas( executor,   records);                        
					}
                    if (shutdown) {
                        future.get();
                        closeConsumer();
                        break;
                    }
				}
                catch (WakeupException wakeupException){
                    logger.warn("wakeupException",wakeupException);
                    closeConsumer();
                    break;
                }
				catch (InterruptException e){
                    logger.warn("InterruptException",e);
					closeConsumer();
					break;
				}
                catch (Exception wakeupException){
                    logger.warn("Exception",wakeupException);
                    closeConsumer();
                    break;
//                    throw new KafkaConsumerException(wakeupException);                    
                }
			}
		}
        catch (KafkaConsumerException e){
            logger.warn("KafkaConsumerException",e);
        }
		catch (Throwable e){
//			if(logger.isErrorEnabled())
//				logger.error("",e);
            logger.warn("Throwable",e);
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
			throw new KafkaConsumerException(e);
		} catch (Throwable e) {
            throw new KafkaConsumerException(e);
		}
	}
	protected Future handleDatas(ExecutorService executor , final ConsumerRecords<Object, Object> records){
        Future future = null;
		if(executor != null) {
            future = executor.submit(new Runnable() {
				@Override
				public void run() {
					doHandle( records);
				}
			});
            
		}
		else{
			doHandle( records);            
		}
        if(!autoCommit) {
            kafkaConsumer.commitSync();
            if(logger.isDebugEnabled())
                logger.debug("Commit kafkaconsumer offset:{}",this.workThreadname);
        }
        return future;

	}
//	protected abstract void handleData(BaseKafkaConsumer consumer,ConsumerRecord<Object, Object> record)  throws Exception;


	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
