package org.frameworkset.plugin.kafka;

import java.util.ArrayList;
import java.util.List;

import kafka.message.MessageAndMetadata;

public abstract class BaseStoreService implements StoreService{
	/**
	 * 批处理接口
	 */
	public void store(List<MessageAndMetadata<byte[], byte[]>> message)  throws Exception {
		
	}
	/**
	 * 实时处理接口
	 */
	public void store(MessageAndMetadata<byte[], byte[]> message)  throws Exception {
		List<MessageAndMetadata<byte[], byte[]>> msgs= new ArrayList<MessageAndMetadata<byte[], byte[]>>();
		msgs.add(message);
		store(msgs) ;
	}
	public abstract void closeService();
}
