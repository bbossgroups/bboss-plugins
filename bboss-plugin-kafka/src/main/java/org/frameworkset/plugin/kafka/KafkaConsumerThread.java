package org.frameworkset.plugin.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class KafkaConsumerThread extends BaseKafkaConsumerThread{
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerThread.class);
	 
//	
//	private HDFSService logstashService;
	public KafkaConsumerThread(KafkaStream<byte[], byte[]> stream,StoreService storeService) {
		super(stream,  storeService);		
	}

	 
	protected   void handleData(MessageAndMetadata<byte[], byte[]> mam)  throws Exception{		 
		storeService.store(mam);
	}
}
