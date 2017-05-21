package org.frameworkset.plugin.kafka;

import kafka.message.MessageAndMetadata;

public interface StoreService {
	public void store(MessageAndMetadata<byte[], byte[]> message)  throws Exception ;
}
