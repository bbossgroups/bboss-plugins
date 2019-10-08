package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.support.ApplicationObjectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;



public class KafkaProductor extends ApplicationObjectSupport {
	private KafkaProducer<Object, Object> producer = null;
	private static final Logger logger = LoggerFactory.getLogger(KafkaProductor.class);
	private final AtomicInteger rejectedExecutionCount = new AtomicInteger(0);
	private Properties productorPropes;
	private boolean sendDatatoKafka = false;
	/**
	 * 异步方式发送消息
	 */
	private boolean sendAsyn = false;

	public KafkaProductor() {
		 
//		Properties props = new Properties();
//		props.put("zookeeper.connect", "hadoop85:2181,hadoop86:2181,hadoop88:2181");
//		
////		props.put("zookeeper.connect", "localhost:2181");
//		
//		// 指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
////		props.put("serializer.class", "org.apache.kafka.common.serialization.StringDeserializer");
////		props.put("serializer.class", "org.apache.kafka.common.serialization.StringDeserializer");
//		props.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
//		props.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
//		    
//		// 同步还是异步，默认2表同步，1表异步。异步可以提高发送吞吐量，但是也可能导致丢失未发送过去的消息
//		props.put("producer.type", "sync");
//
//		// 是否压缩，默认0表示不压缩，1表示用gzip压缩，2表示用snappy压缩。压缩后消息中会有头来指明消息压缩类型，故在消费者端消息解压是透明的无需指定。
//		props.put("compression.codec", "1");
//
//		// 指定kafka节点列表，用于获取metadata(元数据)，不必全部指定
//		props.put("bootstrap.servers", "hadoop85:9092,hadoop86:9092,hadoop88:9092");
//		
//		producer = new KafkaProducer<String, Object>(props);
		
		
	}
	
	public void init(){
//		Properties props = new Properties();
//		props.put("zookeeper.connect", "hadoop85:2181,hadoop86:2181,hadoop88:2181");
//		
////		props.put("zookeeper.connect", "localhost:2181");
//		
//		// 指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
////		props.put("serializer.class", "org.apache.kafka.common.serialization.StringDeserializer");
////		props.put("serializer.class", "org.apache.kafka.common.serialization.StringDeserializer");
//		props.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
//		props.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
//		    
//		// 同步还是异步，默认2表同步，1表异步。异步可以提高发送吞吐量，但是也可能导致丢失未发送过去的消息
//		props.put("producer.type", "sync");
//
//		// 是否压缩，默认0表示不压缩，1表示用gzip压缩，2表示用snappy压缩。压缩后消息中会有头来指明消息压缩类型，故在消费者端消息解压是透明的无需指定。
//		props.put("compression.codec", "1");
//
//		// 指定kafka节点列表，用于获取metadata(元数据)，不必全部指定
//		props.put("bootstrap.servers", "hadoop85:9092,hadoop86:9092,hadoop88:9092");
		if(sendDatatoKafka)
			producer = new KafkaProducer<Object, Object>(productorPropes);


	}

	private ExecutorService worker;



	public void send(final String topic, final Object msg){
		send(  topic,    msg,this.sendAsyn);
	}
	public void send(final String topic,final Object key,final Object msg){
		send(  topic,  key,  msg,this.sendAsyn);
	}
	private final static int workerThreadSize = 100;
	private final static int workerThreadQueueSize = 1024;
	private  ExecutorService initExecutorService(){
		if(worker != null){
			return worker;
		}
		synchronized (this) {
			if(worker == null) {
				int workerThreadSize = super.getApplicationContext().getIntProperty("workerThreadSize",KafkaProductor.workerThreadSize);
				int workerThreadQueueSize = super.getApplicationContext().getIntProperty("workerThreadQueueSize",KafkaProductor.workerThreadQueueSize);;
				final ExecutorService worker_ = ExecutorFactory.newFixedThreadPool(workerThreadSize, workerThreadQueueSize, "Producer-Worker", true);
				BaseApplicationContext.addShutdownHook(new Runnable() {
					@Override
					public void run() {
						worker_.shutdown();
					}
				});
				worker = worker_;
			}
		}
		return worker;
	}
	public void send(final String topic, final Object msg,boolean sendAsyn){
		if(sendDatatoKafka && producer != null){
			if(!sendAsyn) {
				producer.send(new ProducerRecord<Object, Object>(topic, null,msg));
			}
			else
			{
				try{
					initExecutorService();
					this.worker.execute(new Runnable() {
						@Override
						public void run() {
							producer.send(new ProducerRecord<Object, Object>(topic, null,msg));
						}
					});
				} catch (RejectedExecutionException ree) {
					handleRejectedExecutionException(ree);
				}
			}
		}
	}

	private void handleRejectedExecutionException(RejectedExecutionException ree) {
		final int error = rejectedExecutionCount.incrementAndGet();
		final int mod = 100;
		if ((error % mod) == 0) {
			this.logger.warn("RejectedExecutionCount={}", error);
		}
	}
	public void send(final String topic,final Object key,final Object msg,boolean sendAsyn){
		if(sendDatatoKafka && producer != null){
			if(!sendAsyn) {
				producer.send(new ProducerRecord<Object, Object>(topic, key, msg));
			}
			else
			{
				try{
					initExecutorService();
					this.worker.execute(new Runnable() {
						@Override
						public void run() {
							producer.send(new ProducerRecord<Object, Object>(topic, key, msg));
						}
					});
				} catch (RejectedExecutionException ree) {
					handleRejectedExecutionException(ree);
				}
			}
		}
	}

	public Properties getProductorPropes() {
		return productorPropes;
	}

	public void setProductorPropes(Properties productorPropes) {
		this.productorPropes = productorPropes;
	}

	public boolean isSendDatatoKafka() {
		return sendDatatoKafka;
	}

	public void setSendDatatoKafka(boolean sendDatatoKafka) {
		this.sendDatatoKafka = sendDatatoKafka;
	}
	
	 

	
}
