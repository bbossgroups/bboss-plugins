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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: AbstractTemplate.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * bboss workgroup
 * </p>
 * <p>
 * Copyright (c) 2007
 * </p>
 * 
 * @Date 2010-1-13 下午04:14:43
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class AbstractTemplate implements org.frameworkset.spi.DisposableBean {
	// protected boolean transacted = false;

	// protected int destinationType = MQUtil.TYPE_QUEUE;

	// protected String requestMessageSelector;

	// protected String responseMessageSelector;
 
	protected static Logger logger = LoggerFactory.getLogger(AbstractTemplate.class);
	protected ConnectionFactory connectionFactory;

	protected Connection connection;
	protected Session session;
	 

	// protected RequestDispatcher responseDispatcher;

	// protected String replyto;

	// protected int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

 
	protected List<ReceiveDispatcher> tempdispatcher = new ArrayList<ReceiveDispatcher>();

	public AbstractTemplate(JMSConnectionFactory connectionFactory) throws JMSException {
		this(connectionFactory.getConectionFactory());

	}

 
	public AbstractTemplate(ConnectionFactory connectionFactory) throws JMSException {
		 
		// this.replyto = replyto;

		// this.responseMessageSelector = responseMessageSelector;

		if (connectionFactory instanceof ConnectionFactoryWrapper)
			this.connectionFactory = connectionFactory;
		else
			this.connectionFactory = new ConnectionFactoryWrapper(connectionFactory, null);

//		connection = this.connectionFactory.createConnection();	
//		connection.start();
	}

	public void destroy() throws Exception {
		this.stop();

	}
	protected void initSession(boolean transacted,int acknowledgeMode) throws JMSException {
		if(connection == null){
			connection = this.connectionFactory.createConnection();	
			connection.start();
		}
		if(session == null)
			session = connection.createSession(transacted, acknowledgeMode);
	}
	
	protected void initSession(boolean transacted,int acknowledgeMode,String clientid) throws JMSException {
		if(connection == null){
			connection = this.connectionFactory.createConnection();	
			connection.setClientID(clientid);
			connection.start();
		}
		if(session == null)
			session = connection.createSession(transacted, acknowledgeMode);
	}
	private boolean stoped;
	public void stop() {
		if(stoped)
			return;
		 for(ReceiveDispatcher receiveDispatcher:this.tempdispatcher){
			 receiveDispatcher.stop();
		 }
		// if(this.responseDispatcher != null)
		// {
		// this.responseDispatcher.stop();
		// }
		 if(this.session != null){
			 try {
				this.session.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 session = null;
		 }
		if (this.connection != null){
			
			try {
				connection.stop();

			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				this.connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		

		
		stoped = true;
	}

	public javax.jms.Message receiveNoWait(String destination) throws javax.jms.JMSException {
		ReceiveDispatcher dispatcher = null;
		try {
			initSession(false,1);
			MessageSession messageSession = new DefaultMessageAction(session);
			dispatcher = new ReceiveDispatcher(messageSession);
			Message msg = dispatcher.receiveNoWait(destination);
			return msg;
		} finally {
			if (dispatcher != null) {
				dispatcher.stop();
			}
			 this.session = null;
		}
	}

 
 
 

	public javax.jms.Message receive(String destination) throws javax.jms.JMSException {

		ReceiveDispatcher dispatcher = null;
		try {
			initSession(false,1);
			MessageSession messageSession = new DefaultMessageAction(session);
			dispatcher = new ReceiveDispatcher(messageSession);
			Message msg = dispatcher.receive(destination);
			return msg;
		} finally {
			if (dispatcher != null) {
				dispatcher.stop();
			}
			 this.session = null;
		}

	}

	public javax.jms.Message receive(String destination, long timeout) throws javax.jms.JMSException {
		ReceiveDispatcher dispatcher = null;
		try {
			initSession(false,1);
			MessageSession messageSession = new DefaultMessageAction(session);
			dispatcher = new ReceiveDispatcher(messageSession);
			Message msg = dispatcher.receive(destination,timeout);
			return msg;
		} finally {
			if (dispatcher != null) {
				dispatcher.stop();
			}
			 this.session = null;
		}
		
		
	}

	  // Method descriptor #10 (Ljavax/jms/MessageListener;)V
 

	public void setMessageListener(String destination, javax.jms.MessageListener listener)
			throws javax.jms.JMSException {
		ReceiveDispatcher dispatcher = null;
		try {
			initSession(false,1);
			MessageSession messageSession = new DefaultMessageAction(session);
			dispatcher = new ReceiveDispatcher(messageSession);
			if (listener instanceof JMSMessageListener) {
				JMSMessageListener temp = (JMSMessageListener) listener;
				temp.setReceivor(dispatcher);
			}
			dispatcher.setMessageListener(destination,listener);

			tempdispatcher.add(dispatcher);
		} finally {
			dispatcher = null;
			// dispatcher.stop();
		}

	}

	public void receive(String destination, javax.jms.MessageListener listener) throws javax.jms.JMSException {
		setMessageListener(destination, listener);

	}
 
	public void send(String destination, String message) throws JMSException {
		send(destination, message, false);
		// session.createProducer(arg0)
	}

	public void send(String destination, String message, boolean persistent) throws JMSException {
		send(MQUtil.TYPE_QUEUE, destination, message, persistent,null);

		// session.createProducer(arg0)
	}

	public void send(int desttype, String destination, String message) throws JMSException {
		send(desttype, destination, message, false,null);
		// session.createProducer(arg0)
	}
	
	public void send(int desttype, String destination, String message,boolean persistent) throws JMSException {
		send(desttype, destination, message, persistent,null);
		// session.createProducer(arg0)
	}

	

	 

	/**
	 * 单/批处理发送消息api

	 * @param callback
	 * @throws JMSException
	 */
	public void send(SendCallback callback) throws JMSException {
		DefaultMessageAction defaultMessageAction =  null;
		try
		{
			initSession(!callback.autocommit(),callback.ackMode());
			defaultMessageAction = new DefaultMessageAction(session,callback);
			defaultMessageAction.sendMessage();
			defaultMessageAction.commit();
		}
		catch(JMSException e)
		{
			if(defaultMessageAction != null){
				defaultMessageAction.rollback();
			}
			throw e;
		}
		catch(Throwable e)
		{
			if(defaultMessageAction != null){
				defaultMessageAction.rollback();
			}
			throw e;
		}
		finally
		{
//			this.session = null;
			if(defaultMessageAction != null){
				
//				defaultMessageAction.close();
				
			}
			
		}
	}
	

	
	public void send(final int desttype, final String destination, final String message, final boolean persistent, final JMSProperties properties)
			throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination, desttype);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				TextMessage message_ = session.createTextMessage(message);
				if(properties != null)
					MQUtil.initMessage(message_, properties);
				producer.send(message_, deliveryMode, -1, -1);
				
			}
        });
		// session.createProducer(arg0)
	}


	
	

	// public void sendReply(Message msg,Logger logger) throws JMSException
	// {
	// this.responseDispatcher.send(msg,logger);
	// }

	// public void sendReply(Message msg,Logger logger) throws JMSException
	// {
	// this.responseDispatcher.send(msg,logger);
	// }



	public void send( final String destination_, final boolean persistent, final int priority, final long timeToLive,
			final Message message) throws JMSException {
		send(MQUtil.TYPE_QUEUE,    destination_,   persistent,  priority,   timeToLive,
				 message,null);
	}
	public void send(final int destinationType, final String destination_,final  boolean persistent, final String message,
			final JMSProperties properties) throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				TextMessage message_ = session.createTextMessage(message);
				if(properties != null)
					MQUtil.initMessage(message_, properties);
				producer.send(message_, deliveryMode, -1, -1);
				
			}
        });
	}
	public void send( final String destination_, final String message,final boolean persistent, final int priority, final long timeToLive) throws JMSException {

		send( MQUtil.TYPE_QUEUE,  destination_,persistent,  message,
				null);
	}
	public void send(final int destinationType, final String destination_, final boolean persistent,final  int priority, final long timeToLive,
			final Message message) throws JMSException {

		send(  destinationType,   destination_,  persistent,   priority,   timeToLive,
				  message, null);
	}

	public void send(final int destinationType, final String destination_, final boolean persistent, final int priority, final long timeToLive,
			final Message message, final JMSProperties properties) throws JMSException {

		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				if(properties != null)
					MQUtil.initMessage(message, properties);
				producer.send(message, deliveryMode, priority, timeToLive);
				
			}
        });
	}

	 

	
	public void send(final int destinationType, final String destination_, final boolean persistent, final Message message)
			throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				producer.send(message, deliveryMode, -1, -1);
				
			}
        });
	}

	public void send(final int destinationType, final String destination_, final boolean persistent, final Message message,
			final JMSProperties properties) throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				if(properties != null)
					MQUtil.initMessage(message, properties);
				producer.send(message, deliveryMode, -1, -1);
				
			}
		});
	}
	 
	public void send(final int destinationType, final String destination_,final boolean persistent, final String message) throws JMSException {
		send(  destinationType, destination_,  persistent,   message,
				null);
	}

	

	public void send(final int destinationType, final String destination_,final  Message message, final boolean persistent,final  int priority,
			final long timeToLive) throws JMSException {
		send(destinationType,  destination_,  message,   persistent,   priority,
				  timeToLive, null);
	}

	public void send(final int destinationType, final String destination_, final Message message, final boolean persistent, final int priority,
			final long timeToLive, final JMSProperties properties) throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				if(properties != null)
					MQUtil.initMessage(message, properties);
				producer.send(message, deliveryMode, priority, timeToLive);
				
			}
        });
	}

	public void send(final int destinationType, final String destination_, final String message, final boolean persistent, final int priority,
			final long timeToLive) throws JMSException {
		send(  destinationType,   destination_, message,   persistent, priority,
				 timeToLive, null);
	}

	public void send(final int destinationType, final String destination_,final  String message, final boolean persistent, final int priority,
			final long timeToLive, final JMSProperties properties) throws JMSException {
		send(new BaseSendCallback() {
			@Override
			public void sendMessage(MessageSession session) throws JMSException {
		        System.out.println("send message to " + destination_
		                    + " build destination end.");						
				MessageProducer producer  = session.createProducer( destination_, destinationType);
				int deliveryMode = persistent ? DeliveryMode.PERSISTENT
						: DeliveryMode.NON_PERSISTENT;
				TextMessage message_ = session.createTextMessage(message);
				if(properties != null)
					MQUtil.initMessage(message_, properties);
				producer.send(message_, deliveryMode, priority, timeToLive);
				
			}
        });
	}

	
}
