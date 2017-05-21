package org.frameworkset.plugin.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class KafkaConsumerThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerThread.class);
	private KafkaStream<byte[], byte[]> stream;
	 
	private StoreService storeService;
//	
//	private HDFSService logstashService;
	ConsumerConnector consumer;
	public KafkaConsumerThread(KafkaStream<byte[], byte[]> stream,StoreService storeService) {
		this.stream = stream;
		this.storeService = storeService;
		
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();		
		logger.debug(Thread.currentThread().getName() + ": dddddddddddddddddddddddddd");
		while (it.hasNext()) {
			MessageAndMetadata<byte[], byte[]> mam = it.next();
			try {
				if(storeService != null)
					storeService.store(mam);
				else
				{
					logger.debug(Thread.currentThread().getName() + ": partition[" + mam.partition() + "]," 
							+ "offset[" + mam.offset() + "], " + new String(mam.message(),"UTF-8"));
				}
				
			} catch (Exception e) {
				logger.error("系统异常：",e);
			}
			catch (Throwable e) {
				logger.error("系统异常：",e);
				break;
			}

		}
	}
}
