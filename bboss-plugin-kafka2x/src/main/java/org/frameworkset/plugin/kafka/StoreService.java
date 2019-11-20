package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface StoreService {
	public void store(List<ConsumerRecord<Object,Object>> messages)  throws Exception ;
	public void store(ConsumerRecord<Object,Object> message)  throws Exception ;
	public void closeService();
}
