package org.frameworkset.plugin.kafka;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import kafka.message.MessageAndMetadata;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/9/28 10:41
 * @author biaoping.yin
 * @version 1.0
 */
public class TestKafkaBatchConsumer2ndStore extends KafkaBatchConsumer2ndStore{
	private static Logger logger = LoggerFactory.getLogger(TestKafkaBatchConsumer2ndStore.class);
	StringDeserializer sd = new StringDeserializer();
	LongDeserializer ld = new LongDeserializer();
	@Override
	public void store(List<MessageAndMetadata<byte[], byte[]>> messages) throws Exception {
		for(MessageAndMetadata<byte[], byte[]> message:messages){
			String data = sd.deserialize(null,message.message());
			//long key = ld.deserialize(null, message.key());
			logger.info("key="+",data="+data
					+",message.partition()="+message.partition()
					+ ",topic:"+message.topic()+",offset:"+message.offset());

		}
//		if(!this.isAutoCommit())
//			this.commitOffset();
	}

	@Override
	public void store(MessageAndMetadata<byte[], byte[]> message) throws Exception {
		String data = sd.deserialize(null,message.message());
		long key = ld.deserialize(null, message.key());
		System.out.println("key="+key+",data="+data);
//		if(!this.isAutoCommit())//如果是手动提交，则需要显示提交消费的commiter
//			this.commitOffset();
	}

	@Override
	public void closeService() {
		sd.close();
		ld.close();
	}
}
