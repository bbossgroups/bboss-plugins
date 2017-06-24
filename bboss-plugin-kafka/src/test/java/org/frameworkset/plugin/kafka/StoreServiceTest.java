package org.frameworkset.plugin.kafka;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import kafka.message.MessageAndMetadata;

public class StoreServiceTest implements StoreService {
	StringDeserializer sd = new StringDeserializer();
	LongDeserializer ld = new LongDeserializer();
	@Override
	public void store(MessageAndMetadata<byte[], byte[]> message) throws Exception {
		
		String data = sd.deserialize(null,message.message());
		long key = ld.deserialize(null, message.key());
		System.out.println("key="+key+",data="+data);
	}

	@Override
	public void closeService() {
		sd.close();
		ld.close();

	}

}
