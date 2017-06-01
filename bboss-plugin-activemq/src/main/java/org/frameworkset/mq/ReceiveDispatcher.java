/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * <p>
 * ReceivorDispatch.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * bboss workgroup
 * </p>
 * <p>
 * Copyright (c) 2009
 * </p>
 * 
 * @Date 2009-11-15
 * @author biaoping.yin
 * @version 1.0
 */
public class ReceiveDispatcher
{
    private static final Logger LOG = LoggerFactory.getLogger(ReceiveDispatcher.class);

    protected MessageSession session;



    protected MessageConsumer consumer;

    // protected ConnectionFactory connectionFactory;
//    protected Connection connection;
    


    // /**
    // * 请求消息选择器
    // */
    // protected String requestMessageSelector;
    // /**
    // * 响应消息选择器
    // */
    // protected String responseMessageSelector;
    protected String messageSelector;

    public ReceiveDispatcher(MessageSession session) throws JMSException
    {
        this.session = session;
//        if (this.clientid != null && !this.clientid.equals(""))
//            this.connection.setClientID(clientid);
        // connection.start();


    }



    private void assertStarted() throws JMSException
    {
        if (this.session == null)
        {
            throw new JMSException("MQClient has not been started.");
        }

    }


    public MessageConsumer getConsumer(String destination,int destinationType) throws JMSException
    {
        if (destinationType == MQUtil.TYPE_ROUTER)
            throw new JMSException("对不起,不能对路由节点发送消息.type=" + MQUtil.getTypeDesc(destinationType));
        if (consumer != null)
            return consumer;

        return consumer = getConsumer(destinationType, destination);

    }

    public MessageConsumer getConsumerWithSelector(String destination,int destinationType,String selector) throws JMSException
    {

        if (destinationType == MQUtil.TYPE_ROUTER)
            throw new JMSException("对不起,不能对路由节点发送消息.type=" + MQUtil.getTypeDesc(destinationType));
        if (this.messageSelector != null && this.messageSelector.equals(selector))// 如果已经指定了本类代表的目标地址的选择器目标，则直接返回该地址
            return this.consumer;
        if (this.consumer != null)
            throw new JMSException("对不起目标地址已经被其他的选择器使用。other selector is " + this.messageSelector);

        this.consumer = getConsumer(destinationType, destination, selector, false);
        this.messageSelector = selector;


        return this.consumer;
    }

    public MessageConsumer getConsumer(int destinationType, String destination_) throws JMSException
    {
        return getConsumer(destinationType, destination_, this.messageSelector);
    }

    // private Object lock = new Object();

    public MessageConsumer getConsumer(Destination destination) throws JMSException
    {
        return getConsumer(destination, null, false);
    }

    public MessageConsumer getConsumer(Destination destination, String messageSelector, boolean noLocal)
            throws JMSException
    {

        assertStarted();
        MessageConsumer consumer = null;
        // String key = destination.toString() + "$" + noLocal;
        //
        // if (messageSelector != null && !messageSelector.equals(""))
        // {
        // key += "$" + messageSelector;
        // }
        // else
        // {
        // // consumer = consumers.get(key);
        // }
        // if(consumer != null)
        // return consumer;
        // synchronized(lock)
        {
            // consumer = consumers.get(key);
            // if(consumer != null)
            // return consumer;
            // if (consumer == null) {
            consumer = session.createConsumer(destination, messageSelector, noLocal);
            consumer = new JMSMessageConsumer(consumer, this);

            // consumers.put(key, consumer);
            // }
        }
        return consumer;
    }

    public MessageConsumer getConsumer(int destinationType, String destination_, String messageSelector, boolean noLocal)
            throws JMSException
    {
        assertStarted();
        Destination destination = null;
//        if (destinationType == MQUtil.TYPE_QUEUE)
//        {
//            destination = session.createQueue(destination_);
//        }
//        else
//        {
//            destination = session.createTopic(destination_);
//        }
        
        LOG.debug("send message to " + destination_
                + " build destination");
      destination = session.createDestination( destination_, destinationType);
      LOG.debug("send message to " + destination_
                + " build destination end.");

        return getConsumer(destination, messageSelector, noLocal);
    }

    public MessageConsumer getConsumer(int destinationType, String destination_, String messageSelector)
            throws JMSException
    {

        return getConsumer(destinationType, destination_, messageSelector, false);
    }

    // private Object lock1 = new Object();

    public TopicSubscriber getTopicSubscriber(Topic destination, String name) throws JMSException
    {
        return getTopicSubscriber(destination, name, null);
    }

    public TopicSubscriber getTopicSubscriber(Topic destination, String name, String selector) throws JMSException
    {
        assertStarted();
        // String key = destination + "$" + name;
        TopicSubscriber consumer = null;
        // topicSubscribers.get(key);

        // synchronized (lock1)
        {
            // consumer = topicSubscribers.get(key);
            // if (consumer != null)
            // return consumer;
            // if (consumer == null)
            {
                consumer = session.createDurableSubscriber(destination, name, selector, false);
                 consumer = new JMSTopicSubscriber(consumer,this);
                // this.mqclient,this);
                // topicSubscribers.put(key, consumer);
            }
        }
        return consumer;
    }

    public TopicSubscriber getTopicSubscriber(String destination, String name) throws JMSException
    {
        return getTopicSubscriberWithSelector(destination, name, null);
    }

    public TopicSubscriber getTopicSubscriberWithSelector(String destination, String name, String selector)
            throws JMSException
    {
        assertStarted();

        // String key = destination + "$" + name;
        TopicSubscriber consumer = null;// topicSubscribers.get(key);
        // String key = destination + "$" + name;
        // if (consumer != null)
        // return consumer;
        // synchronized (lock1)
        {
            // consumer = topicSubscribers.get(key);
            // if (consumer != null)
            // return consumer;
            Topic topic = this.session.createTopic(destination);            
            // if (consumer == null)
            {
                consumer = session.createDurableSubscriber(topic, name, selector, false);
                consumer = new JMSTopicSubscriber(consumer,this);
                
            }
        }
        return consumer;
    }

    public TopicSubscriber getTopicSubscriberWithSelector(Topic topic, String name, String selector)
            throws JMSException
    {
        assertStarted();

        // String key = destination + "$" + name;
        TopicSubscriber consumer = null;// topicSubscribers.get(key);
        // String key = destination + "$" + name;
        // if (consumer != null)
        // return consumer;
        // synchronized (lock1)
        {
            // consumer = topicSubscribers.get(key);
            // if (consumer != null)
            // return consumer;
            // Topic topic = this.session.createTopic(destination);
            // if (consumer == null)
            {
                consumer = session.createDurableSubscriber(topic, name, selector, false);
                 consumer = new JMSTopicSubscriber(consumer,this);
                // this.mqclient,this);
                // topicSubscribers.put(key, consumer);
            }
        }
        return consumer;
    }

    // public TopicSubscriber getTopicSubscriber(String name) throws
    // JMSException
    // {
    // if (this.client.getDestinationType() != ClientHelper.TYPE_TOPIC)
    // throw new JMSException("对不起,只能对主题节点创建持久订阅消费者.type=" +
    // MQClient.getTypeDesc(client.getDestinationType()));
    // TopicSubscriber topicSubscriber =
    // getTopicSubscriberWithSelector(this.client.getDestination(), name, null);
    // this.consumer = topicSubscriber;
    // return topicSubscriber;
    // }

    // public TopicSubscriber getTopicSubscriberWithSelector(String name, String
    // selector) throws JMSException
    // {
    // if (this.client.getDestinationType() != ClientHelper.TYPE_TOPIC)
    // throw new JMSException("对不起,不能对队列类型节点创建持久订阅消费者.type=" +
    // MQClient.getTypeDesc(this.client.getDestinationType()));
    // this.consumer =
    // getTopicSubscriberWithSelector(this.client.getDestination(), name,
    // selector);
    // return
    // (TopicSubscriber)consumer;//getTopicSubscriberWithSelector(this.client.getDestination(),
    // name, selector);
    // }
//    private boolean stopped = false;
//
//    public void stop()
//    {
//        stop(true);
//    }
//
//    public void stop(boolean remove)
//    // throws JMSException
//    {
//        if (stopped)
//            return;
//        if (this.session != null)
//        {
//            if (this.transacted
//            // && !this.rollbacked
//            // && !this.commited
//            )
//            {
//                try
//                {
//                    this.rollback();
//                }
//                catch (JMSException e)
//                {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//            try
//            {
//                session.close();
//            }
//            catch (Exception e)
//            {
//
//            }
//        }
//        // this.mqclient.removeReceivor(this);
//        this.stopped = true;
//    }
//
//    boolean rollbacked = false;
//
//    public void rollback() throws JMSException
//    {
//        // if(commited || this.rollbacked)
//        // return;
//        if (this.transacted)
//        {
//
//            session.rollback();
//
//            rollbacked = true;
//        }
//
//    }
//
//    boolean commited = false;
//
//    public void commit() throws JMSException
//    {
//        // if(commited || this.rollbacked)
//        // return;
//        if (this.transacted)
//        {
//
//            this.session.commit();
//
//            this.commited = true;
//        }
//
//    }

    private void assertConsumerNull() throws JMSException
    {
        if (this.consumer == null)
        {
            throw new JMSException("consumer == null");
        }
    }

  

    

    // Method descriptor #10 (Ljavax/jms/MessageListener;)V
    public void setMessageListener(String dest,javax.jms.MessageListener listener) throws javax.jms.JMSException
    {
        this.getConsumer(dest,MQUtil.TYPE_QUEUE);
        assertConsumerNull();
        this.consumer.setMessageListener(listener);

    }
    public void setMessageListener(String dest,int desttype,javax.jms.MessageListener listener) throws javax.jms.JMSException
    {
        this.getConsumer(dest,desttype);
        assertConsumerNull();
        this.consumer.setMessageListener(listener);

    }

   

    // Method descriptor #12 ()Ljavax/jms/Message;
    public javax.jms.Message receive(String dest) throws javax.jms.JMSException
    {
        this.getConsumer(dest,MQUtil.TYPE_QUEUE);
        assertConsumerNull();

        return this.consumer.receive();
    }

    public javax.jms.Message receive(String dest,long timeout) throws javax.jms.JMSException
    {
        this.getConsumer(dest,MQUtil.TYPE_QUEUE);
        assertConsumerNull();

        return this.consumer.receive(timeout);
    }

    public javax.jms.Message receiveNoWait(String dest) throws javax.jms.JMSException
    {
        this.getConsumer(dest,MQUtil.TYPE_QUEUE);
        assertConsumerNull();
        return this.consumer.receiveNoWait();
    }
    
    public boolean isClientAcknowledge() throws JMSException
    {
        assertStarted();
        return session.getAcknowledgeMode() == Session.CLIENT_ACKNOWLEDGE;
    }
    
    public void unsubscribe(String unsubscribename) throws JMSException
    {
        assertStarted();
        this.session.unsubscribe(unsubscribename);
    }
    
    public void stop() {
    	try {
    		if(consumer != null)
    			this.consumer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    }

}
