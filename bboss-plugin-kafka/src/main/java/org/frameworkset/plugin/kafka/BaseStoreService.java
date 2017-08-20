package org.frameworkset.plugin.kafka;

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
		
	}
	public abstract void closeService();
}
