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

package org.frameworkset.mq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * <p>Title: JMSReceiveTemplate.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2007</p>
 * @Date 2010-1-13 下午04:11:12
 * @author biaoping.yin
 * @version 1.0
 */
public class JMSReceiveTemplate extends AbstractTemplate
{

   
    public JMSReceiveTemplate(ConnectionFactory connectionFactory)
            throws JMSException
    {
        super(connectionFactory);
        // TODO Auto-generated constructor stub
    } 
    

   
   

   
    public JMSReceiveTemplate(JMSConnectionFactory connectionFactory) throws JMSException
    {
        super(connectionFactory);
        // TODO Auto-generated constructor stub
    }
    
    
    
    
    public TopicSubscriber getTopicSubscriber(String clientid,String destination, String name) throws JMSException
    {
        return getTopicSubscriberWithSelector(  clientid,destination, name, null);
    }
    
    public void subscribeTopic(String clientid,String destination, String name,MessageListener listener) throws JMSException
    {
        getTopicSubscriberWithSelector(  clientid,destination, name, null).setMessageListener(listener);
    }
    public void subscribeTopicWithSelector(String clientid,String destination, String name,String selector,MessageListener listener) throws JMSException
    {
        getTopicSubscriberWithSelector(  clientid,destination, name, selector).setMessageListener(listener);
    }
    
  
    public TopicSubscriber getTopicSubscriber(String clientid,Topic destination, String name) throws JMSException
    {
        return getTopicSubscriber(  clientid,destination, name, null);
    }
    public TopicSubscriber getTopicSubscriber(String clientid,Topic destination, String name, String selector) throws JMSException
    {
        ReceiveDispatcher dispatcher = null;
        try
        {
        	initSession(false,1,clientid);
			MessageSession messageSession = new DefaultMessageAction(session);
            dispatcher = new ReceiveDispatcher(messageSession);
            TopicSubscriber sub = dispatcher.getTopicSubscriberWithSelector(destination, name, selector);
            this.tempdispatcher.add(dispatcher);
            return sub;
        }
        catch(JMSException e)
        {
            if(dispatcher != null)
                dispatcher.stop();
            throw e;
        }
//        return this.requestDispatcher.getTopicSubscriber(destination, name, selector);
    }
    public TopicSubscriber getTopicSubscriberWithSelector(String clientid,String destination, String name, String selector)
    throws JMSException
    {
        ReceiveDispatcher dispatcher = null;
        try
        {
        	
        	initSession(false,1,clientid);
			MessageSession messageSession = new DefaultMessageAction(session);
            dispatcher = new ReceiveDispatcher(messageSession);
            TopicSubscriber sub = dispatcher.getTopicSubscriberWithSelector(destination, name, selector);
            this.tempdispatcher.add(dispatcher);
            return sub;
        }
        catch(JMSException e)
        {
            if(dispatcher != null)
                dispatcher.stop();
            throw e;
        }
        
        
    }



    
    

    public void unsubscribe(String destination ,String unsubscribename) throws JMSException
    {
    	ReceiveDispatcher requestDispatcher = null;
         try
         {
        	 initSession(false,1);
 			 MessageSession messageSession = new DefaultMessageAction(session);
        	 requestDispatcher = new ReceiveDispatcher(messageSession);
        	 requestDispatcher.unsubscribe(unsubscribename);
         }
        
         finally
         {
        	 if(requestDispatcher != null)
            	 requestDispatcher.stop();
         }
    }
   

    

}
