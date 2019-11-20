package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;


public class TestKafkaConsumer {

	public static void main(String[] args) {
		BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("kafkaconfumer.xml");
		KafkaListener consumer = context.getTBeanObject("kafkaconsumer", KafkaListener.class);
		Thread t = new Thread(consumer);
		t.start();

	}

}
