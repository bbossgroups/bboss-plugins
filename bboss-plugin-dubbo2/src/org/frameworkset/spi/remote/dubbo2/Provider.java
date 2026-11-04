package org.frameworkset.spi.remote.dubbo2;


public class Provider {
	private static volatile boolean running = true;
//	public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";
	
	public static void main(String[] args) {
		DubboHandlerContainer container = new DubboHandlerContainer("dubbo-service.xml");
		container.initDubboServices();
		synchronized (Provider.class) {
			while (running) {
				try {
					Provider.class.wait();
				} catch (Throwable e) {
				}
			}
		}
	}
	
	/**
	 * 通过加载dubbo服务配置文件dubbo-service.xml，启动dubbo服务
	 */
	public static void startServer() {
		 
		startServer("dubbo-service.xml");	
	}
	
	/**
	 * 通过加载dubbo服务配置文件providerConfig，启动dubbo服务
	 * @param providerConfig
	 */
	public static void startServer(String providerConfig) {
		DubboHandlerContainer container = new DubboHandlerContainer(providerConfig);
		container.initDubboServices();
		synchronized (Provider.class) {
			while (running) {
				try {
					Provider.class.wait();
				} catch (Throwable e) {
				}
			}
		}
	}
	
}
