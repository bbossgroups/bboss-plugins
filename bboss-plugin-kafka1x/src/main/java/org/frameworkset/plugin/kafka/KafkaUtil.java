package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

import java.util.concurrent.ExecutorService;

public class KafkaUtil {
	private static BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("kafka.xml");
	private final static int workerThreadSize = 100;
	private final static int workerThreadQueueSize = 10240;
	private static ExecutorService worker;
	public static KafkaProductor getKafkaProductor(String name){
		return context.getTBeanObject(name, KafkaProductor.class);
		
	}
	public static String getProperty(String name){
		return context.getProperty(name);
	}

	public static String getProperty(String name,String defaultValue){
		return context.getProperty(name,defaultValue);
	}

	public static ExecutorService getExecutorService(){
		if(worker != null){
			return worker;
		}
		synchronized (KafkaUtil.class) {
			if(worker == null) {
				int workerThreadSize = context.getIntProperty("workerThreadSize",KafkaUtil.workerThreadSize);
				int workerThreadQueueSize = context.getIntProperty("workerThreadQueueSize",KafkaUtil.workerThreadQueueSize);;
				final ExecutorService worker_ = ExecutorFactory.newFixedThreadPool(workerThreadSize, workerThreadQueueSize, "Producer-Worker", true);
				BaseApplicationContext.addShutdownHook(new Runnable() {
					@Override
					public void run() {
						worker_.shutdown();
					}
				});
				worker = worker_;
			}
		}
		return worker;
	}

}
