package org.frameworkset.plugin.kafka;

public class TestKafka {
	public static void main(String[] args){
		TestKafka testKafka = new TestKafka();
		testKafka.testSend();
	}
	public void testSend() {
		KafkaUtil kafkaUtil = new KafkaUtil("kafka_2.12-2.3.0/kafka.xml");
		KafkaProductor productor = kafkaUtil.getProductor("kafkaproductor");
		for (int i = 0; i < 100000; i++){
			productor.send("blackcat", (long)i, "aaa" + i);
			productor.send("blackcat", (long)i, "bbb" + i);
			productor.send("blackcatbatchstore", (long)i, "aaa" + i);
			productor.send("blackcatbatchstore", (long)i, "bbb" + i);
			productor.send("blackcatstore", (long)i, "aaa" + i);
			productor.send("blackcatstore", (long)i, "bbb" + i);
		}


//		//异步方式发送消息
//		productor.send("blackcat",3l,"aaa",true);
//		productor.send("blackcat",4l,"bbb",true);
//
//		//异步方式发送消息
//		productor.send("blackcat",5l,"aaa",false);
//		productor.send("blackcat",6l,"bbb",false);
	}
}
