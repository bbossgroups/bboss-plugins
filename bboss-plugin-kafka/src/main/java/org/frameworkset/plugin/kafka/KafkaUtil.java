package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

public class KafkaUtil {
	private static BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("kafka.xml");
	
	public static KafkaProductor getKafkaProductor(String name){
		return context.getTBeanObject(name, KafkaProductor.class);
		
	}
	public static String getProperty(String name){
		return context.getProperty(name);
	}

	public static String getProperty(String name,String defaultValue){
		return context.getProperty(name,defaultValue);
	}

}
