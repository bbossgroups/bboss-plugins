package org.frameworkset.spi.remote.dubbo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.frameworkset.spi.assemble.Pro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;




public class DubboHandlerContainer {
	private DubboPluginConfig dubboPluginConfig;
	private BaseApplicationContext context;
	private static final Logger log = LoggerFactory.getLogger(DubboHandlerContainer.class);
	private Map<String,Pro> dubboServices = new HashMap<String,Pro>();
	private static final int JAVA_SPEC_VERSION = resolveJavaSpecVersion();

	private static int resolveJavaSpecVersion() {
		String version = System.getProperty("java.specification.version");
		if (version == null) {
			return 8;
		}
		try {
			if (version.startsWith("1.")) {
				return Integer.parseInt(version.substring(2));
			}
			int dotIndex = version.indexOf('.');
			String major = dotIndex > 0 ? version.substring(0, dotIndex) : version;
			return Integer.parseInt(major);
		} catch (Exception e) {
			return 8;
		}
	}

	/**
	 * JDK 16 开始默认启用强封装，javassist 通过反射访问 ClassLoader#defineClass 会被拒绝，
	 * dubbo 动态生成 Protocol$Adaptive、Wrapper、Proxy 等类时必然失败；
	 * 这里提前检测并给出可操作的提示，避免直接抛出难以定位的 ExceptionInInitializerError。
	 */
	private static void checkJavassistModuleAccess() {
		if (JAVA_SPEC_VERSION < 16) {
			return;
		}
		boolean opened;
		try {
			// JDK 9+ 才有模块体系，这里全部用反射访问，保证在 JDK 8 上也能编译和运行
			Class<?> moduleClass = Class.forName("java.lang.Module");
			Object javaBaseModule = Class.class.getMethod("getModule").invoke(Object.class);
			Object currentModule = Class.class.getMethod("getModule").invoke(DubboHandlerContainer.class);
			opened = (Boolean) moduleClass.getMethod("isOpen", String.class, moduleClass)
					.invoke(javaBaseModule, "java.lang", currentModule);
		} catch (Throwable ignore) {
			// 探测失败时不阻断启动，交由 dubbo 自身处理
			return;
		}
		if (!opened) {
			throw new IllegalStateException("当前 JDK 版本为 " + System.getProperty("java.specification.version")
					+ "，java.base 未向当前模块开放 java.lang 包的深度反射访问，"
					+ "dubbo 依赖 javassist 动态生成 Protocol$Adaptive、Wrapper、Proxy 等类，dubbo 服务无法暴露。"
					+ "请在启动脚本或 IDE 运行配置的 VM options 中添加：--add-opens=java.base/java.lang=ALL-UNNAMED；"
					+ "也可以改用 JDK 8 或 JDK 11 运行。");
		}
	}

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
		checkJavassistModuleAccess();
		if(context != null)
		{
			
			Set<String> beanNames = context.getPropertyKeys();
			if(beanNames == null || beanNames.size() == 0)
				return ;
			// Take any bean name that we can determine URLs for.
			Iterator<String> beanNamesItr = beanNames.iterator();
			Pro pro = null;
			String providerVersion = dubboPluginConfig.getDubboProviderVersion();
			while(beanNamesItr.hasNext()) {
				String beanName = beanNamesItr.next();
				try
				{
					pro = context.getProBean(beanName);
					if(pro == null)
						continue;
					String enabledubbo = pro.getStringExtendAttribute("dubbo:enable");
					String interfaceClass = pro.getStringExtendAttribute("dubbo:interface");
					if(enabledubbo == null ){
						if(interfaceClass == null)
							continue;
					}
					if(enabledubbo.equals("false") )
					{
						continue;
					}
					
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
					if(version != null && version.length() > 0) {
						service.setVersion(version);
					}
					else if(providerVersion != null && providerVersion.length() > 0){
						service.setVersion(providerVersion);
					}
					 
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
