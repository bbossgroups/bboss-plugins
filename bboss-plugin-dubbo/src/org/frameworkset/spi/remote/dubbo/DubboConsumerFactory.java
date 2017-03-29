package org.frameworkset.spi.remote.dubbo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.BeanInfoAware;

import com.alibaba.dubbo.config.ReferenceConfig;

public class DubboConsumerFactory extends BeanInfoAware {
	private String reference;
	private String version;
	private static final Log log = LogFactory.getLog(DubboConsumerFactory.class);
	public Object buildDubboConsumer(){
		BaseApplicationContext context = super.beaninfo.getApplicationContext();
		DubboPluginConfig pluginConfig = null;
		try {
			pluginConfig = context.getTBeanObject("dubboconfig", DubboPluginConfig.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		if(pluginConfig == null)
			pluginConfig = DubboPluginConfig.getDefaultDubboPluginConfig();
		ReferenceConfig reference_ = new ReferenceConfig(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference_.setApplication(pluginConfig.getApplication());
		reference_.setRegistry(pluginConfig.getRegistry()); // 多个注册中心可以用setRegistries()
		reference_.setInterface(reference);
		if(version != null)
			reference_.setVersion(version);
		// 和本地bean一样使用xxxService
		return reference_.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
	}
	

}
