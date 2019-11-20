package org.frameworkset.plugin.kafka;

import java.util.List;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import kafka.message.MessageAndMetadata;

public class StoreServiceTest extends BaseStoreService {
	StringDeserializer sd = new StringDeserializer();
	LongDeserializer ld = new LongDeserializer();
	@Override
	public void store(List<MessageAndMetadata<byte[], byte[]>> messages) throws Exception {
		for(MessageAndMetadata<byte[], byte[]> message:messages){
			String data = sd.deserialize(null,message.message());
			long key = ld.deserialize(null, message.key());
			System.out.println("key="+key+",data="+data);
		}
	}

	@Override
	public void closeService() {
		sd.close();
		ld.close();
	}

	@Override
	public void store(MessageAndMetadata<byte[], byte[]> message) throws Exception {
		String data = sd.deserialize(null,message.message());
		long key = ld.deserialize(null, message.key());
		System.out.println("key="+key+",data="+data);
	}

}
