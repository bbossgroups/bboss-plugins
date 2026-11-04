package org.frameworkset.spi.remote.dubbo2;

public class TestProvider implements TestProviderInf{
  public String test(String hello){
	  return hello;
  }
}
