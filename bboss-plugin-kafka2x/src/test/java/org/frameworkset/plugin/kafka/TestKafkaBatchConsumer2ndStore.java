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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/9/28 10:41
 * @author biaoping.yin
 * @version 1.0
 */
public class TestKafkaBatchConsumer2ndStore extends KafkaBatchConsumer2ndStore{
	@Override
	public void store(ConsumerRecords<Object, Object> messages) throws Exception {
		for(ConsumerRecord<Object,Object> message:messages){
			Object data = message.value();
			Object key =  message.key();
			System.out.println("key="+key+",data="+data+",topic="+message.topic()+",partition="+message.partition()+",offset="+message.offset());
		}
	}



	@Override
	public void store(ConsumerRecord<Object,Object> message) throws Exception {
		Object data = message.value();
		Object key =  message.key();
		System.out.println("key="+key+",data="+data+",topic="+message.topic()+",partition="+message.partition()+",offset="+message.offset());
	}
}
