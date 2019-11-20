package org.frameworkset.plugin.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;


public class StoreServiceTest extends BaseStoreService {

	@Override
	public void store(List<ConsumerRecord<Object,Object>> messages) throws Exception {
		for(ConsumerRecord<Object,Object> message:messages){
			Object data = message.value();
			Object key =  message.key();
			System.out.println("key="+key+",data="+data+",topic="+message.topic()+",partition="+message.partition()+",offset="+message.offset());
		}
	}

	@Override
	public void closeService() {

	}

	@Override
	public void store(ConsumerRecord<Object,Object> message) throws Exception {
		Object data = message.value();
		Object key =  message.key();
		System.out.println("key="+key+",data="+data+",topic="+message.topic()+",partition="+message.partition()+",offset="+message.offset());
	}

}
