package org.frameworkset.plugin.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerThread extends BaseKafkaConsumerThread{
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerThread.class);
	 
//	
//	private HDFSService logstashService;
	public KafkaConsumerThread(BaseKafkaConsumer consumer,String[] topic, StoreService storeService,long timeOut) {
		super(consumer,topic,"KafkaConsumerThread-"+topic[0],  timeOut,storeService);
	}

	 
	protected   void handleData(BaseKafkaConsumer consumer, ConsumerRecord<Object, Object> record)  throws Exception{
		storeService.store(record);
	}
}
