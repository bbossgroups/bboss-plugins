package org.frameworkset.spi.remote.dubbo;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

public class Consumer {
	public static void main(String[] args){
		BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("dubbo-consumer.xml");
		TestProviderInf testProviderInf = context.getTBeanObject("testservice", TestProviderInf.class);
		System.out.println(testProviderInf.test("hi,dubbo!"));
	}

}
