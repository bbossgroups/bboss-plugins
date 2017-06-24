package org.frameworkset.plugin.kafka;

import org.junit.Test;

public class TestKafka {
	@Test
	public void testSend(){
		KafkaProductor productor = KafkaUtil.getKafkaProductor("kafkaproductor");
		productor.send("blackcat",1l,"aaa");
		productor.send("blackcat",1l,"bbb");
	}
}
