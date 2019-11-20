package org.frameworkset.plugin.kafka;

import kafka.message.MessageAndMetadata;

import java.util.List;

public interface StoreService {
	public void store(List<MessageAndMetadata<byte[], byte[]>> messages)  throws Exception ;
	public void store(MessageAndMetadata<byte[], byte[]> message)  throws Exception ;
	public void closeService();
}
