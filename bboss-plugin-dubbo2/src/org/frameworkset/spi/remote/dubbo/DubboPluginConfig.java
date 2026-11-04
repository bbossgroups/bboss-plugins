package org.frameworkset.spi.remote.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.frameworkset.util.SimpleStringUtil;

public class DubboPluginConfig {
	private ApplicationConfig application;
	private String applicationName;
	private String applicationOwner;
	private String registryAddress;
	private String registryUserName;
	private String registryPassword;
	private String dubboProviderVersion;	
	private int applicationQosPort;
	
	private boolean applicationQosEnable;
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
		if(SimpleStringUtil.isNotEmpty(registryUserName)) {
			registry.setUsername(registryUserName);
		}
		if(SimpleStringUtil.isNotEmpty(registryPassword)) {
			registry.setPassword(registryPassword);
		}
		if(dubboProviderVersion != null && !dubboProviderVersion.equals("")){
			registry.setVersion(dubboProviderVersion);
		}
		application.setQosEnable(applicationQosEnable);
		if(applicationQosPort > 0){
			application.setQosPort(applicationQosPort);
			
		}
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
	
	public void setApplication(ApplicationConfig application) {
		this.application = application;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getRegistryAddress() {
		return registryAddress;
	}
	
	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	
	public String getRegistryUserName() {
		return registryUserName;
	}
	
	public void setRegistryUserName(String registryUserName) {
		this.registryUserName = registryUserName;
	}
	
	public String getRegistryPassword() {
		return registryPassword;
	}
	
	public void setRegistryPassword(String registryPassword) {
		this.registryPassword = registryPassword;
	}
	
	public String getDubboProviderVersion() {
		return dubboProviderVersion;
	}
	
	public void setDubboProviderVersion(String dubboProviderVersion) {
		this.dubboProviderVersion = dubboProviderVersion;
	}
	
	public String getProtocolPort() {
		return protocolPort;
	}
	
	public void setProtocolPort(String protocolPort) {
		this.protocolPort = protocolPort;
	}
	
	public String getProtocolName() {
		return protocolName;
	}
	
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	
	public int getProtocolThreads() {
		return protocolThreads;
	}
	
	public void setProtocolThreads(int protocolThreads) {
		this.protocolThreads = protocolThreads;
	}
	
	public void setApplicationQosEnable(boolean applicationQosEnable) {
		this.applicationQosEnable = applicationQosEnable;
	}
	
	public void setApplicationQosPort(int applicationQosPort) {
		this.applicationQosPort = applicationQosPort;
	}
}
