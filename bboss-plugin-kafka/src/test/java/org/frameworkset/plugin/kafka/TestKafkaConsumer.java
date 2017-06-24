package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;


public class TestKafkaConsumer {

	public static void main(String[] args) {
		BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("kafkaconfumer.xml");
		KafkaConsumer consumer = context.getTBeanObject("kafkaconsumer", KafkaConsumer.class);
		Thread t = new Thread(consumer);
		t.start();

	}

}
