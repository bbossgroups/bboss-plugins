package org.frameworkset.spi.remote.dubbo;

public class TestProvider implements TestProviderInf{
  public String test(String hello){
	  return hello;
  }
}
