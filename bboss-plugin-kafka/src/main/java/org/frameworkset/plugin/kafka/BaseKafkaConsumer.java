package org.frameworkset.plugin.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.support.ApplicationObjectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class BaseKafkaConsumer extends ApplicationObjectSupport implements KafkaListener {
//	private BaseApplicationContext applicationContext;
	protected ConsumerConfig consumerConfig;
	protected String topic;
//	private String zookeeperConnect;
	protected Properties productorPropes;
	

	protected int partitions = 4;

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
//        props.put("auto.commit.enable", true);
//        
//        props.put("auto.commit.interval.ms", "1000"); 
//        props.put("auto.offset.reset", "smallest");
//        props.put("application.id", "logstash_app");
//        
//        props.put("value.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
//		props.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
       
	}
	public void init(){
		 consumerConfig = new ConsumerConfig(productorPropes);
	}
	protected StoreService storeService = null;

	@Override
	public void run() {
		
	    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
	    String[] topics = topic.split("\\,");
	    int a_numThreads = partitions;
	    for(String t:topics)
	    {
	    	String[] infos = t.split(":");
	    	topicCountMap.put(infos[0], new Integer(a_numThreads));
	    }
		final ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
	    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
	    final ExecutorService executor = Executors.newFixedThreadPool(a_numThreads*topics.length+10,new ThreadFactory(){

			@Override
			public Thread newThread(Runnable r) {

				return new Thread(r,r.toString());
			}
		});

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
	    BaseApplicationContext.addShutdownHook(new Runnable() {
			@Override
			public void run() {
				executor.shutdown();
				consumer.shutdown();
				storeService.closeService();
			}
		});

	}
	
	protected abstract Runnable buildRunnable(KafkaStream<byte[], byte[]> stream ,String topic);

}
