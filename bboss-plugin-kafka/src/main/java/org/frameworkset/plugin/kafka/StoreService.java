package org.frameworkset.plugin.kafka;

import java.util.List;

import kafka.message.MessageAndMetadata;

public interface StoreService {
	public void store(List<MessageAndMetadata<byte[], byte[]>> message)  throws Exception ;
	public void store(MessageAndMetadata<byte[], byte[]> message)  throws Exception ;
	public void closeService();
}
