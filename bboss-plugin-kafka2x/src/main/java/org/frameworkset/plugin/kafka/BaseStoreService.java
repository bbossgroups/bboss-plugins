package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseStoreService implements StoreService{
	/**
	 * 批处理接口
	 */
	public void store(List<ConsumerRecord<Object,Object>> message)  throws Exception {
		
	}
	/**
	 * 实时处理接口
	 */
	public void store(ConsumerRecord<Object,Object> message)  throws Exception {
		List<ConsumerRecord<Object,Object>> msgs= new ArrayList<ConsumerRecord<Object,Object>>();
		msgs.add(message);
		store(msgs) ;
	}
	public abstract void closeService();
}
