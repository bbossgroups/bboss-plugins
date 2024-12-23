package org.frameworkset.rocketmq;

import org.frameworkset.spi.InitializingBean;

public abstract class RocketmqConsumer2ndStore<T> extends BaseRocketMQConsumer implements StoreService<T>, InitializingBean {

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied
	 * (and satisfied BeanFactoryAware and ApplicationContextAware).
	 * <p>This method allows the bean instance to perform initialization only
	 * possible when all bean properties have been set and to throw an
	 * exception in the event of misconfiguration.
	 * @throws Exception in the event of misconfiguration (such
	 * as failure to set an essential property) or if initialization fails.
	 */
	public void afterPropertiesSet() throws Exception{
		init();
		this.storeService = this;
	}



}
