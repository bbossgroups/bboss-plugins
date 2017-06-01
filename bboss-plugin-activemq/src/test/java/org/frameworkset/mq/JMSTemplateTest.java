package org.frameworkset.mq;
/*
 *  Copyright 2008 biaoping.yin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

/**
 * <p>Title: JMSTemplateTest.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2007</p>
 * @Date 2009-11-24 下午02:57:10
 * @author biaoping.yin
 * @version 1.0
 */
public class JMSTemplateTest
{
	private BaseApplicationContext context ;
	@Before
	public void init()
	{
		context =  DefaultApplicationContext.getApplicationContext("org/frameworkset/mq/manager-jmstemplate-test.xml");
	}
	 @Test
	 /**
	  * 不建议使用群发模式
	  */
    public void test()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
        	for(int i =0 ; i < 1000; i ++)
        		template.send("atest", "ahello "+ i);
            
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
        
    }

    @Test
    public void testSendCallback()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
            template.send(new BaseSendCallback() {
				@Override
				public void sendMessage(MessageSession session) throws JMSException {
			       				
					MessageProducer producer  = session.createProducer( "atest", MQUtil.TYPE_QUEUE);
					for(int i =0 ; i < 1000; i ++){
						String message = "ahello "+ i;
						 producer.send(session.createTextMessage(message),0,-1,10000l);
					}
					 System.out.println("send message to " + "atest"
			                    + " build destination end.");		
					
				}

				@Override
				public boolean autocommit() {
					// TODO Auto-generated method stub
					return false;
				}
            });


        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
    }
	 
	 @Test
	    public void testgwcs()
	    {
	        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
	        try
	        {
	            template.send("atest", "ahello");
	            
	        }
	        catch (JMSException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        finally
	        {
	            template.stop();
	        }
	        
	    }
	 @Test
    public  void testPooledFactoryConnection()
    {
        JMSConnectionFactory factory = context.getTBeanObject("test.amq.PooledConnectionFactory",JMSConnectionFactory.class);
        try
        {
            Connection connection = factory.getConnection();
            connection.start();
            connection.close();
            
            connection = factory.getConnection();
            connection.start();       
            connection.close();
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
	 @Test
    public  void testFactoryConnection()
    {
        JMSConnectionFactory factory = context.getTBeanObject("test.amq.ConnectionFactory",JMSConnectionFactory.class);
        try
        {
            Connection connection = factory.getConnection();
            connection.start();
            connection.stop();
            connection.close();
            
            connection = factory.getConnection();
           
            connection.start();   
            connection.stop();
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
	 @Test
    public  void testPersistentMessage()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
            template.send("atest", "phello",true);
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
        
    }
	 @Test
    public  void testAllparamsMessage()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
            template.send("atest", "allhello",true,4,10000);
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
        
    }
    
	 @Test
    public  void testReceiveMessage()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
            Message msg = template.receive("atest");
            System.out.println("testReceiveMessage:"+ msg);
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
        
    }
	 
	 @Test
	    public  void testReceiveGWCSMessage()
	    {
	     JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
	        try
	        {
	            for(int i = 0; i < 10; i++)
	            {
	                
    	            Message msg = template.receive("GWCS.Receive.Cache");
    	            System.out.println("testReceiveMessage:"+ msg);
    	            
	            }
	        }
	        catch (JMSException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        finally
	        {
	            template.stop();   
	        }
	        
	    }
	    
	    
	    
    
    
    
	 @Test
    public  void testMessageListener()
    {
        JMSReceiveTemplate template = context.getTBeanObject("test.jms.receive.template",JMSReceiveTemplate.class);
        try
        {
            template.setMessageListener("atest",new MessageListener() {

                public void onMessage(Message arg0)
                {
                    System.out.println("msg comming:"+arg0);
                }                
            });
            
        }
        catch (Exception e)
        {
            
            template.stop();
        }
        finally
        {

        }
        
    }
	 @Test
    public  void testTopicSend()
    {
        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
        try
        {
        	for(int i = 0; i <10000; i ++)
        		template.send(MQUtil.TYPE_TOPIC,"getsubtest", "getsubtest_"+i,true,0,1000l);
            
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            template.stop();
        }
        
    }
	 
	 @Test
	    public  void testTopicSendCallback()
	    {
	        JMSTemplate template = context.getTBeanObject("test.jmstemplate",JMSTemplate.class);
	        try
	        {
	        	
	        		template.send(new BaseSendCallback() {
						
						@Override
						public void sendMessage(MessageSession session) throws JMSException {
							MessageProducer producer  = session.createProducer( "getsubtest", MQUtil.TYPE_TOPIC);
							for(int i =0 ; i < 10000; i ++){
								String message = "ahello "+ i;
								 producer.send(session.createTextMessage(message),0,-1,10000l);
							}
							 System.out.println("send message to " + "atest"
					                    + " build destination end.");		
							
						}

						@Override
						public boolean autocommit() {
							// TODO Auto-generated method stub
							return false;
						}
					});
	            
	        }
	        catch (JMSException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        finally
	        {
	            template.stop();
	        }
	        
	    }
//	 @Test
//    public  void testsubscriberSend()
//    {
//        JMSTemplate template = (JMSTemplate)BaseSPIManager.getBeanObject("test.jmstemplate");
//        try
//        {
//            template.send("subtest1", "subtest");
//            
//        }
//        catch (JMSException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        finally
//        {
//            template.stop();
//        }
//        
//    }
	 @Test
    public  void testGetSubscriber()
    {
        JMSReceiveTemplate template = context.getTBeanObject("test.topic.receive.jmstemplate",JMSReceiveTemplate.class);
        try
        {
            template.getTopicSubscriber("clientid1","getsubtest", "subscribename").setMessageListener(new MessageListener() {

                public void onMessage(Message arg0)
                {
                    System.out.println("topic msg comming:"+arg0);
                }                
            });
            
        }
        catch (Exception e)
        {
            
            template.stop();
        }
        finally
        {

        }
        System.out.println();
        
        
    }
	
    public void testSubscriber() throws Exception
    {
        JMSReceiveTemplate template = context.getTBeanObject("test.topic1.receive.jmstemplate",JMSReceiveTemplate.class);
        try
        {
            template.subscribeTopic("clientid","getsubtest", "duoduo",new MessageListener() {

                public void onMessage(Message arg0)
                {
                    System.out.println("topic msg comming:"+arg0);
                    
                }                
            });
            
        }
        catch (Exception e)
        {
            
            template.stop();
            throw e;
        }
        finally
        {

        }
    }
	 
	 @Test
	    public void testunSubscriber()
	    {
	        JMSReceiveTemplate template = context.getTBeanObject("test.topic1.receive.jmstemplate",JMSReceiveTemplate.class);
	        try
	        {
	            template.unsubscribe("topic1","subscribename");
	            
	        }
	        catch (Exception e)
	        {
	            
	            template.stop();
	        }
	        finally
	        {

	        }
	    }
    
    public static void main(String[] args) throws Exception
    {
    	
        JMSTemplateTest JMSTemplateTest = new JMSTemplateTest();
        JMSTemplateTest.init();
        JMSTemplateTest.testSendCallback();
        JMSTemplateTest.testTopicSendCallback();
//        testPooledFactoryConnection();
//        testPooledFactoryConnection();
        
//        testTopicSend();
//        testGetSubscriber();
        
        
//        testReceiveTopicMessage();
//        testPersistentMessage();
//        testAllparamsMessage();
//        testReceiveMessage();
////        System.exit(0);
//        JMSTemplateTest.testMessageListener();
        JMSTemplateTest.testSubscriber();
//        testTopicSend();
//        testsubscriberSend();
//        testSubscriber();
//        testGetSubscriber();
        
        
        
    }
}
