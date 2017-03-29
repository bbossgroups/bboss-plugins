package org.frameworkset.spi.remote.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboPluginConfig {
	private ApplicationConfig application;
	private String applicationName;
	private String applicationOwner;
	private String registryAddress;
	private String registryUserName;
	private String registryPassword;
	private RegistryConfig registry;
	private static DubboPluginConfig defaultDubboPluginConfig;
	public static synchronized DubboPluginConfig getDefaultDubboPluginConfig(){
		if(defaultDubboPluginConfig == null){
			DubboPluginConfig _defaultDubboPluginConfig = new DubboPluginConfig();
			_defaultDubboPluginConfig.application = new ApplicationConfig();
			_defaultDubboPluginConfig.registry = new RegistryConfig();
			_defaultDubboPluginConfig.protocol = new ProtocolConfig();
			defaultDubboPluginConfig = _defaultDubboPluginConfig;
		}
		return defaultDubboPluginConfig;
	}
	public RegistryConfig getRegistry() {
		return registry;
	}
	private String protocolPort = null;
	private String protocolName = "dubbo";
	private int protocolThreads ;
	private ProtocolConfig protocol = null;
	public ProtocolConfig getProtocol() {
		return protocol;
	}
	public void init(){
		// 当前应用配置
		application = new ApplicationConfig();
		application.setName(applicationName);
		 
		// 连接注册中心配置
		registry = new RegistryConfig();
		registry.setAddress(registryAddress);
		registry.setUsername(registryUserName);
		registry.setPassword(registryPassword);
		if(protocolName != null){ 
			// 服务提供者协议配置
			protocol = new ProtocolConfig();
			protocol.setName(protocolName);
			if(protocolPort != null)
				protocol.setPort(Integer.parseInt(protocolPort));
			if(protocolThreads > 0)
				protocol.setThreads(protocolThreads);
		}
	}
	public ApplicationConfig getApplication() {
		return application;
	}
	public String getApplicationOwner() {
		return applicationOwner;
	}
	public void setApplicationOwner(String applicationOwner) {
		this.applicationOwner = applicationOwner;
	}
	
	

}
