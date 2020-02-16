package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.support.ApplicationObjectSupport;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

//import kafka.consumer.ConsumerConfig;
//
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;

public class BaseKafkaConsumer extends ApplicationObjectSupport implements KafkaListener {
//	private BaseApplicationContext applicationContext;
	protected List<BaseKafkaConsumerThread> baseKafkaConsumerThreadList = new ArrayList<>();
	protected String topic;

	public Properties getConsumerPropes() {
		return consumerPropes;
	}

	//	private String zookeeperConnect;
	protected Properties consumerPropes;
	private boolean autoCommit;
//	private KafkaConsumer consumer;
	protected long pollTimeout = 1000l;
	protected int threads = 4;
	protected Boolean batch = true;
	protected ExecutorService executor;
	protected String keyDeserializer;
	protected String valueDeserializer;
	protected Integer maxPollRecords;
	protected Integer workThreads ;
	protected Integer workQueue = 100;
	private String groupId;
	public void setThreads(int threads) {
		this.threads = threads;
	}
	protected String discardRejectMessage;
	public String getDiscardRejectMessage() {
		return discardRejectMessage;
	}

	public void setDiscardRejectMessage(String discardRejectMessage) {
		this.discardRejectMessage = discardRejectMessage;
	}

	public int getThreads() {
		return threads;
	}

	public String getTopic() {
		return topic;
	}

	public void setBatch(Boolean batch) {
		this.batch = batch;
	}

	public Boolean getBatch() {
		return batch;
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

	public void setKeyDeserializer(String keyDeserializer) {
		this.keyDeserializer = keyDeserializer;
	}

	public String getKeyDeserializer() {
		return keyDeserializer;
	}

	public void setValueDeserializer(String valueDeserializer) {
		this.valueDeserializer = valueDeserializer;
	}

	public String getValueDeserializer() {
		return valueDeserializer;
	}



	public long getPollTimeout() {
		return pollTimeout;
	}

	public void setMaxPollRecords(Integer maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	public Integer getMaxPollRecords() {
		return maxPollRecords;
	}
//	public KafkaConsumer getConsumer() {
//		return consumer;
//	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setPollTimeout(long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}



	public void setConsumerPropes(Properties consumerPropes) {
		this.consumerPropes = consumerPropes;
	}

	//	private ConsumerConnector consumer;
	public void shutdown(){
		if(baseKafkaConsumerThreadList.size() > 0){
			for(BaseKafkaConsumerThread baseKafkaConsumerThread:baseKafkaConsumerThreadList){
				baseKafkaConsumerThread.shutdown();
			}
		}
		if(executor != null)
			executor.shutdown();
//		if(consumer != null)
//			consumer.close();
//		if(storeService != null)
//			storeService.closeService();
	}

//	String topic,String zookeeperConnect, HDFSService logstashService
	
	public BaseKafkaConsumer() {
//		this.topic = topic;
//		props = new Properties();    
//		this.zookeeperConnect = zookeeperConnect;
//		props.put("zookeeper.connect", this.zookeeperConnect); 
//		this.logstashService = logstashService;
//        
////		props.put("zookeeper.connect", "localhost:2181");    
////        props.put("zookeeper.connectiontimeout.ms", "30000");    
//        props.put("group.id","logstash");  
//        props.put("zookeeper.session.timeout.ms", "1000");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.commit.enable", true);enable.auto.commit
//        
//        props.put("auto.commit.interval.ms", "1000"); 
//        props.put("auto.offset.reset", "smallest");
//        props.put("application.id", "logstash_app");
//        
//        props.put("value.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
//		props.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
       
	}
	public void init(){
		String _autoCommit = this.consumerPropes.getProperty("enable.auto.commit","true");
		if(_autoCommit.equals("true")){
			this.autoCommit = true;
		}
		else{
			this.autoCommit = false;
		}
//		 consumerConfig = new ConsumerConfig(productorPropes);
	}
	protected StoreService storeService = null;


	@Override
	public void run() {

	    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
	    final String[] topics = topic.split("\\,");
	    int a_numThreads = threads;
//	    for(String t:topics)
//	    {
//	    	String[] infos = t.split(":");
//	    	topicCountMap.put(infos[0], new Integer(a_numThreads));
//	    }
		executor = Executors.newFixedThreadPool(a_numThreads,new ThreadFactory(){
			private int i = 0;
			@Override
			public Thread newThread(Runnable r) {

				return new Thread(r,"BaseKafkaConsumer-"+(i ++));
			}
		});
		for(int i = 0; i < a_numThreads; i ++) {
			BaseKafkaConsumerThread runnable =buildRunnable(i,topics);
			baseKafkaConsumerThreadList.add(runnable);
//			Runnable runnable = new Runnable() {
//				@Override
//				public void run() {
//					consumer = new KafkaConsumer(productorPropes);
//					consumer.subscribe(Arrays.asList(topics));
//
////					Map<String, List<PartitionInfo>> listMap = consumer.listTopics();
//
//					while (true) {
//						ConsumerRecords<Object, Object> records = consumer.poll(1000l);
//						List<ConsumerRecord<Object, Object>> precords = records.records((TopicPartition) null);
//						for (ConsumerRecord<Object, Object> record : records)
//							System.out.printf("offset = %d, key = %s, value = %s%", record.offset(), record.key(), record.value());
//					}
//				}
//			};
			executor.submit(runnable);

		}

//	    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

/**
		for(String t:topics)
	    {
	    	String[] infos = t.split(":");
		    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(infos[0]);
//		    System.out.println("-----------------------------------------:"+infos[0]+"-------------"+streams);
			int i = 0;
		    for (final KafkaStream<byte[], byte[]> stream : streams) {
//				StoreService storeService = this.getApplicationContext().getTBeanObject("storeService",StoreService.class);
//				if(this.storeService == null)
//					this.storeService = storeService;
		        executor.submit(buildRunnable(stream,infos[0]+"-"+i));
		    }
	    }
 */
	    BaseApplicationContext.addShutdownHook(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		});

	}
	
	protected BaseKafkaConsumerThread buildRunnable(int partition,String[] topic){
		BaseKafkaConsumerThread baseKafkaConsumerThread = new BaseKafkaConsumerThread(partition,this,topic,storeService);
		baseKafkaConsumerThread.setKeyDeserializer(keyDeserializer);
		baseKafkaConsumerThread.setValueDeserializer(valueDeserializer);
		baseKafkaConsumerThread.setMaxPollRecords(maxPollRecords);
		baseKafkaConsumerThread.setPollTimeout(pollTimeout);
		baseKafkaConsumerThread.setWorkThreads(workThreads);
		baseKafkaConsumerThread.setWorkQueue(workQueue);
		baseKafkaConsumerThread.setBatch(batch);
		baseKafkaConsumerThread.setDiscardRejectMessage(discardRejectMessage);
		baseKafkaConsumerThread.setGroupId(groupId);
		return baseKafkaConsumerThread;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

//	/**
//	 *
//	 */
//	public void commitOffset(){
//		this.consumer.commitSync();
//	}
//
//	public void commitOffsets(boolean retryOnFailure){
//		this.consumer.commitSync();
////		this.consumer.commitOffsets(retryOnFailure);
//	}
//
//	/**
//	 *  Commit offsets using the provided offsets map
//	 *
//	 *  @param offsetsToCommit a map containing the offset to commit for each partition.
//	 *  @param retryOnFailure enable retries on the offset commit if it fails.
//	 */
//	public void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsetsToCommit, boolean retryOnFailure){
//		this.consumer.commitSync(offsetsToCommit);
//	}

}
