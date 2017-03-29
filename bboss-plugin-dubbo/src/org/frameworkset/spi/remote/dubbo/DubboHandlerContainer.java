package org.frameworkset.spi.remote.dubbo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.frameworkset.spi.assemble.Pro;

import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;




public class DubboHandlerContainer {
	private DubboPluginConfig dubboPluginConfig;
	private BaseApplicationContext context;
	private static final Log log = LogFactory.getLog(DubboHandlerContainer.class);
	private Map<String,Pro> dubboServices = new HashMap<String,Pro>();
	public DubboHandlerContainer(String serviceConfig) {
		
		this.context = DefaultApplicationContext.getApplicationContext(serviceConfig);
		try {
			dubboPluginConfig = context.getTBeanObject("dubboconfig", DubboPluginConfig.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		if(dubboPluginConfig == null)
			dubboPluginConfig = DubboPluginConfig.getDefaultDubboPluginConfig();
	}
	public DubboHandlerContainer(BaseApplicationContext context) {
	
		this.context = context;
	}
	/**
	 * ServiceConfig<XxxService> service = new ServiceConfig<XxxService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
service.setApplication(application);
service.setRegistry(registry); // 多个注册中心可以用setRegistries()
service.setProtocol(protocol); // 多个协议可以用setProtocols()
service.setInterface(XxxService.class);
service.setRef(xxxService);
service.setVersion("1.0.0");
 
// 暴露及注册服务
service.export();
	 */
	public void initDubboServices()
	{
		if(context != null)
		{
			
			Set<String> beanNames = context.getPropertyKeys();
			if(beanNames == null || beanNames.size() == 0)
				return ;
			// Take any bean name that we can determine URLs for.
			Iterator<String> beanNamesItr = beanNames.iterator();
			Pro pro = null;
			while(beanNamesItr.hasNext()) {
				String beanName = beanNamesItr.next();
				try
				{
					pro = context.getProBean(beanName);
					if(pro == null)
						continue;
					boolean enabledubbo = pro.getBooleanExtendAttribute("dubbo:enable",false);
					if(!enabledubbo){
						continue;
					}
					String interfaceClass = pro.getStringExtendAttribute("dubbo:interface");
					String version = pro.getStringExtendAttribute("dubbo:version");
					Object _service = context.getBeanObject(beanName);
					Class serviceapi = null;
					if(interfaceClass != null){
						
						serviceapi = Class.forName(interfaceClass);
					}
					else
					{
						
						Class serviceClass = _service.getClass();
						serviceapi = this.findRemoteAPI(serviceClass);
						interfaceClass = serviceapi.getName();
					}
					this.dubboServices.put(interfaceClass, pro);
					 
					// 服务提供者暴露服务配置
					long start = System.currentTimeMillis();
					ServiceConfig service = new ServiceConfig(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
					service.setApplication(dubboPluginConfig.getApplication());
					service.setRegistry(dubboPluginConfig.getRegistry()); // 多个注册中心可以用setRegistries()
					service.setProtocol(dubboPluginConfig.getProtocol()); // 多个协议可以用setProtocols()
					service.setInterface(serviceapi);
					service.setRef(_service);
					if(version != null)
						service.setVersion(version);
					 
					// 暴露及注册服务
					service.export();
					long end = System.currentTimeMillis();
					log.debug("registry dubbo service ["+beanName+"] finished in "+(end- start)+"ms.");
				}
				catch(Exception e)
				{
					if (log.isErrorEnabled()) 
					{
						log.error("registry dubbo service '" + beanName + "' failed: " + e.getMessage(),e);
					}
				}
				
			}
		}
	}
	
	private Class findRemoteAPI(Class implClass)
	  {
	    if (implClass == null || implClass.equals(GenericService.class))
	      return null;	    
	    Class []interfaces = implClass.getInterfaces();
	    if (interfaces.length == 1)
	      return interfaces[0];

	    return findRemoteAPI(implClass.getSuperclass());
	  }

	public DubboPluginConfig getDubboPluginConfig() {
		return dubboPluginConfig;
	}

	public void setDubboPluginConfig(DubboPluginConfig dubboPluginConfig) {
		this.dubboPluginConfig = dubboPluginConfig;
	}

}
