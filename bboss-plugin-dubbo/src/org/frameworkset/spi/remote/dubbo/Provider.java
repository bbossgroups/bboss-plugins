package org.frameworkset.spi.remote.dubbo;


public class Provider {
	 private static volatile boolean running = true;
	 public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";
		public static void main(String[] args){
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

}
