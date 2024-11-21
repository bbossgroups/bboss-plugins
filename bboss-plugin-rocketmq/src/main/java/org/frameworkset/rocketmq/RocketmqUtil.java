package org.frameworkset.rocketmq;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

public class RocketmqUtil {
	public BaseApplicationContext getContext() {
		return context;
	}

	private  BaseApplicationContext context ;

	private static RocketmqUtil defaultRocketmqUtil;

	public RocketmqUtil(String contextPath){
		context = DefaultApplicationContext.getApplicationContext(contextPath);
	}

	/**
	 *
	 * @param name
	 * @return
	 * @deprecated use follow:
	 * RocketmqUtil rocketmqUtil = new RocketmqUtil("rocketmq/rocketmq.xml");
	 * RocketmqUtil productor = RocketmqUtil.getProductor(String name)
	 */
	public static RocketmqProductor getRocketmqProductor(String name){
		if(defaultRocketmqUtil == null){
			synchronized (RocketmqUtil.class) {
				if(defaultRocketmqUtil == null)
					defaultRocketmqUtil = new RocketmqUtil("rocketmq.xml");
			}
		}
		RocketmqProductor rocketmqProductor = defaultRocketmqUtil.getContext().getTBeanObject(name, RocketmqProductor.class);
		return rocketmqProductor;
	}
	public RocketmqProductor getProductor(String name){

		RocketmqProductor rocketmqProductor = getContext().getTBeanObject(name, RocketmqProductor.class);
		return rocketmqProductor;
	}

    public BaseRocketMQConsumer getRocketmqConsumer(String name){

        BaseRocketMQConsumer kafkaConsumer = getContext().getTBeanObject(name, BaseRocketMQConsumer.class);
        return kafkaConsumer;
    }
	public  String getProperty(String name){
		return context.getProperty(name);
	}

	public  String getProperty(String name,String defaultValue){
		return context.getProperty(name,defaultValue);
	}



}
