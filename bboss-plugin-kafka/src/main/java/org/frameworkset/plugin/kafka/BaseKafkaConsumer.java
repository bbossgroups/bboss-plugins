package org.frameworkset.plugin.kafka;

import kafka.common.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.support.ApplicationObjectSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

//import kafka.consumer.ConsumerConfig;
//
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;

public abstract class BaseKafkaConsumer extends ApplicationObjectSupport implements KafkaListener {
//	private BaseApplicationContext applicationContext;
	protected String topic;

	public Properties getConsumerPropes() {
		return consumerPropes;
	}

	//	private String zookeeperConnect;
	protected Properties consumerPropes;
	private boolean autoCommit;
	private KafkaConsumer consumer;
	protected long pollTimeOut = 1000l;
	protected int partitions = 4;
	protected ExecutorService executor;

	public KafkaConsumer getConsumer() {
		return consumer;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setPollTimeOut(long pollTimeOut) {
		this.pollTimeOut = pollTimeOut;
	}

	public void setPartitions(int partitions) {
		this.partitions = partitions;
	}

	public void setConsumerPropes(Properties consumerPropes) {
		this.consumerPropes = consumerPropes;
	}

	//	private ConsumerConnector consumer;
	public void shutdown(){
		if(executor != null)
			executor.shutdown();
		if(consumer != null)
			consumer.close();
		if(storeService != null)
			storeService.closeService();
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
	    int a_numThreads = partitions;
//	    for(String t:topics)
//	    {
//	    	String[] infos = t.split(":");
//	    	topicCountMap.put(infos[0], new Integer(a_numThreads));
//	    }
		executor = Executors.newFixedThreadPool(a_numThreads*topics.length+10,new ThreadFactory(){

			@Override
			public Thread newThread(Runnable r) {

				return new Thread(r,r.toString());
			}
		});
		for(int i = 0; i < a_numThreads; i ++) {
			Runnable runnable =buildRunnable(topics);
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
	
	protected abstract Runnable buildRunnable(String[] topic);

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 *
	 */
	public void commitOffset(){
		this.consumer.commitSync();
	}

	public void commitOffsets(boolean retryOnFailure){
		this.consumer.commitSync();
//		this.consumer.commitOffsets(retryOnFailure);
	}

	/**
	 *  Commit offsets using the provided offsets map
	 *
	 *  @param offsetsToCommit a map containing the offset to commit for each partition.
	 *  @param retryOnFailure enable retries on the offset commit if it fails.
	 */
	public void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsetsToCommit, boolean retryOnFailure){
		this.consumer.commitSync(offsetsToCommit);
	}

}
