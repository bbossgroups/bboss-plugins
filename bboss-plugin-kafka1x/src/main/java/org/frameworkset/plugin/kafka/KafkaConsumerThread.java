package org.frameworkset.plugin.kafka;


import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerThread extends BaseKafkaConsumerThread{
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerThread.class);
	 
//	
//	private HDFSService logstashService;
	public KafkaConsumerThread(BaseKafkaConsumer consumer,KafkaStream<byte[], byte[]> stream, StoreService storeService, String topic) {
		super(consumer,"KafkaConsumerThread-"+topic,topic,stream,  storeService);
	}

	 
	protected   void handleData(BaseKafkaConsumer consumer,MessageAndMetadata<byte[], byte[]> mam)  throws Exception{
		storeService.store(mam);
	}
}
