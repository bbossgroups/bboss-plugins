package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;



public class KafkaProductor  {
	private KafkaProducer<Object, Object> producer = null;
	private static final Logger logger = LoggerFactory.getLogger(KafkaProductor.class);
	private Properties productorPropes;
	private boolean sendDatatoKafka = false;


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
	public Future<RecordMetadata> send(ProducerRecord<Object, Object> record, Callback callback){
		if(sendDatatoKafka && producer != null){
			return producer.send(record,callback);
		}
		if(logger.isInfoEnabled())
			logger.info("Ignore send Data to Kafka:sendDatatoKafka={} or producer is null",sendDatatoKafka);
		return null;
	}
	public Future<RecordMetadata> send(final String topic, final Object msg, Callback callback){
		if(sendDatatoKafka && producer != null){
			return producer.send(new ProducerRecord<Object, Object>(topic, null,msg),callback);
		}
		if(logger.isInfoEnabled())
			logger.info("Ignore send Data to Kafka:sendDatatoKafka={} or producer is null",sendDatatoKafka);
		return null;
	}
	public Future<RecordMetadata> send(final String topic,final Object key,final Object msg,Callback callback){
		if(sendDatatoKafka && producer != null){
			return producer.send(new ProducerRecord<Object, Object>(topic, key, msg),callback);
		}
		if(logger.isInfoEnabled())
			logger.info("Ignore send Data to Kafka:sendDatatoKafka={} or producer is null",sendDatatoKafka);
		return null;
	}

	public Future<RecordMetadata> send(final String topic, final Object msg){
		return send( topic,  msg, (Callback)null);
	}
	public Future<RecordMetadata> send(final String topic,final Object key,final Object msg){
		return send( topic, key, msg, (Callback)null);
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
