package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

public class KafkaUtil {
	public BaseApplicationContext getContext() {
		return context;
	}

	private  BaseApplicationContext context ;

	private static KafkaUtil defaultKafkaUtil;

	public KafkaUtil(String contextPath){
		context = DefaultApplicationContext.getApplicationContext(contextPath);
	}

	/**
	 *
	 * @param name
	 * @return
	 * @deprecated use follow:
	 * KafkaUtil kafkaUtil = new KafkaUtil("kafka_2.12-2.3.0/kafka.xml");
	 * KafkaProductor productor = kafkaUtil.getProductor(String name)
	 */
	public static KafkaProductor getKafkaProductor(String name){
		if(defaultKafkaUtil == null){
			synchronized (KafkaUtil.class) {
				if(defaultKafkaUtil == null)
					defaultKafkaUtil = new KafkaUtil("kafka.xml");
			}
		}
		KafkaProductor kafkaProductor = defaultKafkaUtil.getContext().getTBeanObject(name, KafkaProductor.class);
		return kafkaProductor;
	}
	public KafkaProductor getProductor(String name){

		KafkaProductor kafkaProductor = getContext().getTBeanObject(name, KafkaProductor.class);
		return kafkaProductor;
	}
	public  String getProperty(String name){
		return context.getProperty(name);
	}

	public  String getProperty(String name,String defaultValue){
		return context.getProperty(name,defaultValue);
	}

}
