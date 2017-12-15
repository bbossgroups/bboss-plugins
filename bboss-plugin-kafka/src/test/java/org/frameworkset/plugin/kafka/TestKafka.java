package org.frameworkset.plugin.kafka;

import org.junit.Test;

public class TestKafka {
	@Test
	public void testSend(){
		KafkaProductor productor = KafkaUtil.getKafkaProductor("kafkaproductor");
		productor.send("blackcat",1l,"aaa");
		productor.send("blackcat",2l,"bbb");
		//异步方式发送消息
		productor.send("blackcat",3l,"aaa",true);
		productor.send("blackcat",4l,"bbb",true);
		
		//异步方式发送消息
		productor.send("blackcat",5l,"aaa",false);
		productor.send("blackcat",6l,"bbb",false);
	}
}
