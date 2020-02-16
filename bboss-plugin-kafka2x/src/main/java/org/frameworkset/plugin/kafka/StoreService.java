package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface StoreService {
	public void store(ConsumerRecords<Object, Object> records)  throws Exception ;
	public void store(ConsumerRecord<Object,Object> message)  throws Exception ;
}
