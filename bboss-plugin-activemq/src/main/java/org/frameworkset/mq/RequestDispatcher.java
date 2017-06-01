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
import java.io.InputStream;



/**
 * <p>
 * RequestDispatcher.java
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
 * @deprecated
 * @Date 2009-11-15
 * @author biaoping.yin
 * @version 1.0
 */
public class RequestDispatcher extends ReceiveDispatcher
{

	public RequestDispatcher(MessageSession session) throws JMSException
	{
	    
		super(session);
		
	}

//	protected boolean	persistent	= false;
//
//	protected int		priovity	= 4;
//
//	protected long		timetolive	= 0L;
//
//	public RequestDispatcher(Connection connection, boolean transacted,
//			int acknowledgeMode, int destinationType, String destination,
//			boolean persistent, int priovity, long timetolive)
//			throws JMSException
//	{
//
//		super(connection, transacted, acknowledgeMode, destinationType,
//				destination);
//		this.persistent = persistent;
//		this.priovity = priovity;
//		this.timetolive = timetolive;
//	}
//
//	public RequestDispatcher(Connection connection, boolean transation,
//			int autoAcknowledge, String destination) throws JMSException
//	{
//
//		super(connection, transation, autoAcknowledge, destination);
//	}
//
//	public RequestDispatcher(Connection connection, boolean transation,
//			int autoAcknowledge, int destType, String destination)
//			throws JMSException
//	{
//
//		super(connection, transation, autoAcknowledge, destType, destination);
//	}
//
//	public RequestDispatcher(Connection connection, boolean transation,
//			int autoAcknowledge, int destType, String destination,
//			String messageSelector) throws JMSException
//	{
//
//		super(connection, transation, autoAcknowledge, destType, destination,
//				messageSelector);
//	}
//
//	public RequestDispatcher(Connection connection, boolean transation,
//            int autoAcknowledge, int destType, String destination,
//            String messageSelector,boolean persistent) throws JMSException
//    {
//
//        super(connection, transation, autoAcknowledge, destType, destination,
//                messageSelector);
//        this.persistent = persistent;
//    }
//
//	public RequestDispatcher(Connection connection) throws JMSException
//	{
//
//		super(connection);
////		this.connection = connection;
//
//	}

	private static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);
//
//	public BytesMessage createBytesMessage() throws JMSException
//	{
//		initSession( transacted,acknowledgeMode);
//		BytesMessage msg = session.createBytesMessage();
//		return msg;
//
//	}
//
//	public ObjectMessage createObjectMessage() throws JMSException
//	{
//
//		initSession( transacted,acknowledgeMode);
//		ObjectMessage msg = session.createObjectMessage();
//		return msg;
//
//	}
//
//	private void assertStarted( ) throws JMSException
//	{
//
//		if (this.session == null)
//		{
//			throw new JMSException("MQClient has not been started.");
//		}
//
//	}
//
//	public ObjectMessage createObjectMessage(java.io.Serializable object)
//			throws JMSException
//	{
//
//		this.initSession( transacted,acknowledgeMode);
//		ObjectMessage msg = session.createObjectMessage(object);
//		return msg;
//
//	}
//
//	public TextMessage createTextMessage() throws JMSException
//	{
//
//		this.initSession( transacted,acknowledgeMode);
//		TextMessage msg = session.createTextMessage();
//		return msg;
//
//	}
//
//	public TextMessage createTextMessage(String msg) throws JMSException
//	{
//
//		this.initSession( transacted,acknowledgeMode);
//		TextMessage msg_ = session.createTextMessage(msg);
//		return msg_;
//
//	}
//
//	public MapMessage createMapMessage() throws JMSException
//	{
//
//		this.initSession( transacted,acknowledgeMode);
//		MapMessage msg = session.createMapMessage();
//
//		return msg;
//
//	}
//
//	public StreamMessage createStreamMessage() throws JMSException
//	{
//
//		this.initSession( transacted,acknowledgeMode);
//		StreamMessage msg = session.createStreamMessage();
//
//		return msg;
//
//	}

	// public Destination createDestination(String Destination) throws
	// JMSException
	// {
	// initSession( transacted,acknowledgeMode);
	// return session.createQueue(Destination);
	//
	// }

	public void send(int destinationType, String destination_,
			boolean persistent, int priority, long timeToLive, Message message,
			Logger step, JMSProperties properties) throws JMSException
	{
	    if(step != null && step.isDebugEnabled())
    		step.debug("send message to " + destination_
    				+ " initSession( transacted,acknowledgeMode),message=" + message);
		MessageProducer producer = null;
		try
		{
			Destination destination = null;
//			boolean isqueue = destinationType == MQUtil.TYPE_QUEUE;
//			if (isqueue)
//			{
//			    if(step != null)
//		            step.logBasic("send message to " + destination_
//						+ " build QUEUE destination");
//				destination = session.createQueue(destination_);
//				if(step != null)
//		            step.logBasic("send message to " + destination_
//						+ " build QUEUE destination end");
//			}
//			else
//			{
//			    if(step != null)
//		            step.logBasic("send message to " + destination_
//						+ " build Topic destination");
//				destination = session.createTopic(destination_);
//				if(step != null)
//		            step.logBasic("send message to " + destination_
//						+ " build Topic destination end");
//			}
			 if(step != null && step.isDebugEnabled())
				 step.debug("send message to " + destination_
                   + " build destination.");
			destination = this.session.createDestination( destination_, destinationType);
			if(step != null && step.isDebugEnabled())
				step.debug("send message to " + destination_
                  + " build destination end");
			int deliveryMode = persistent ? DeliveryMode.PERSISTENT
					: DeliveryMode.NON_PERSISTENT;
			if(step != null && step.isDebugEnabled())
				step.debug("send message to " + destination_
					+ " this.client.isPersistent =" + persistent);
			if(step != null && step.isDebugEnabled())
				step.debug("send message to " + destination
					+ " send started....");
			producer = session.createProducer(destination);
			if(properties != null)
				MQUtil.initMessage(message, properties);
			producer.send(message, deliveryMode, priority, timeToLive);
			if(step != null && step.isDebugEnabled())
				step.debug("send message to " + destination + " send end....");
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Sent! to destination: " + destination + " message: "
						+ message);
			}
		}
		catch (JMSException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JMSException(e.getMessage());
		}
		finally
		{
			if (producer != null)
				try
				{
					producer.close();
				}
				catch (Exception e)
				{

				}

		}
	}

	public void send(int destinationType, String destination_,
			boolean persistent, int priority, long timeToLive, Message message, JMSProperties properties)
			throws JMSException
	{

		LOG.debug("send message to " + destination_
				+ " initSession( transacted,acknowledgeMode),message=" + message);

		MessageProducer producer = null;
		try
		{
			Destination destination = null;
//			destinationType = JMSConnectionFactory.evaluateDestinationType(destination_, destinationType);
	        
//			destination_ = JMSConnectionFactory.evaluateDestination(destination_);
//			boolean isqueue = destinationType == MQUtil.TYPE_QUEUE;
//			if (isqueue)
//			{
//				LOG.debug("send message to " + destination_
//						+ " build QUEUE destination");
//				destination = session.createQueue(destination_);
//				LOG.debug("send message to " + destination_
//						+ " build QUEUE destination end");
//			}
//			else
//			{
//				LOG.debug("send message to " + destination_
//						+ " build Topic destination");
//				destination = session.createTopic(destination_);
//				LOG.debug("send message to " + destination_
//						+ " build Topic destination end");
//			}
			LOG.debug("send message to " + destination_
                  + " build destination");
			destination = session.createDestination(destination_, destinationType);
			LOG.debug("send message to " + destination_
	                  + " build destination end.");
			int deliveryMode = persistent ? DeliveryMode.PERSISTENT
					: DeliveryMode.NON_PERSISTENT;
			LOG.debug("send message to " + destination_
					+ " this.client.isPersistent =" + persistent);
			LOG.debug("send message to " + destination + " send started....");
			producer = session.createProducer(destination);
			if(properties != null)
				MQUtil.initMessage(message, properties);
			producer.send(message, deliveryMode, priority, timeToLive);
			LOG.debug("send message to " + destination + " send end....");
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Sent! to destination: " + destination + " message: "
						+ message);
			}
		}
		catch (JMSException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JMSException(e.getMessage());
		}
		finally
		{
			if (producer != null)
				try
				{
					producer.close();
				}
				catch (Exception e)
				{

				}

		}
	}
	
	
	public void send(int destinationType, String destination_,
            boolean persistent, int priority, long timeToLive, String message,JMSProperties properties)
            throws JMSException
    {
	    TextMessage message_ = session.createTextMessage(message);
	    send( destinationType,  destination_,
	             persistent,  priority,  timeToLive,  message_, properties);
           
    }

//	public void send(Message message, Logger logger,JMSProperties properties) throws JMSException
//	{
//
//		if (this.destinationType == MQUtil.TYPE_ROUTER)
//			throw new JMSException("对不起,不能对路由节点发送消息.type="
//					+ MQUtil.getTypeDesc(destinationType));
//		// send(destinationType, this.persistent,4,0L,message, logger);
//		send(destinationType, destination, persistent, message, logger, properties);
//	}

//	public void send(int destinationType, String destination_,
//			boolean persistent, Message message, Logger logger, JMSProperties properties)
//			throws JMSException
//	{
//
//		send(destinationType, destination_, persistent, this.priovity,
//				this.timetolive, message, logger, properties);
//	}

//	public void send(int destinationType, String destination_,
//			boolean persistent, Message message,JMSProperties properties) throws JMSException
//	{
//
//		send(destinationType, destination_, persistent, this.priovity,
//				this.timetolive, message,properties);
//	}
//
//	public void send(int destinationType, String destination_,
//            boolean persistent, String message,JMSProperties properties) throws JMSException
//    {
//
//        send(destinationType, destination_, persistent, this.priovity,
//                this.timetolive, message, properties);
//    }
//
//	public void send(Message message, JMSProperties properties) throws JMSException
//	{
//
//		if (this.destinationType == MQUtil.TYPE_ROUTER)
//			throw new JMSException("对不起,不能对路由节点发送消息.type="
//					+ MQUtil.getTypeDesc(destinationType));
//		send(destinationType, this.destination, persistent, message,properties);
//	}
//
//	public void send(DefaultMessageAction defaultMessageAction ,String destination,int destinationType) throws JMSException
//	{
//
////		initSession( transacted,acknowledgeMode);
//		MessageProducer producer = null;
//		try
//		{
//			Destination destination_ = session.createDestination(destination,destinationType);
//
////			if (this.destinationType == MQUtil.TYPE_QUEUE)
////			{
////				destination = session.createQueue(this.destination);
////			}
////			else
////			{
////				destination = session.createTopic(this.destination);
////			}
//			
//			LOG.debug("send message to " + destination
//	                  );
////            destination = session.createDestination( destination_, destinationType);
//
//			producer = session.createProducer(destination_);
//
//
//			defaultMessageAction.sendMessage(defaultMessageAction,producer);
//		}
//		catch (JMSException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new JMSException(e.getMessage());
//		}
//		finally
//		{
//			if (producer != null)
//				try
//				{
//					producer.close();
//				}
//				catch (Exception e)
//				{
//
//				}
//
//		}
//
//	}

	public void send(String msg,String destination, int destinationType,boolean persistent,int priovity, long timetolive, JMSProperties properties) throws JMSException
	{

//		initSession( transacted,acknowledgeMode);
		MessageProducer producer = null;
		try
		{

//			if (this.destinationType == MQUtil.TYPE_QUEUE)
//			{
//				destination = session.createQueue(this.destination);
//			}
//			else
//			{
//				destination = session.createTopic(this.destination);
//			}

			LOG.debug("send message to " + destination
					+ " build destination");
			Destination destination_ = session.createDestination( destination, destinationType);

			int deliveryMode = persistent ? DeliveryMode.PERSISTENT
					: DeliveryMode.NON_PERSISTENT;
			producer = session.createProducer(destination_);
			Message message = session.createTextMessage(msg);
			if(properties != null)
				MQUtil.initMessage(message, properties);
			producer
					.send(message, deliveryMode, priovity, timetolive);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Sent! to destination: " + destination + " message: "
						+ message);
			}
		}
		catch (JMSException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JMSException(e.getMessage());
		}
		finally
		{
			if (producer != null)
				try
				{
					producer.close();
				}
				catch (Exception e)
				{

				}

		}

	}

	public void send(int destinationType, String destination_, Message message,
			boolean persistent, int priority, long timeToLive, JMSProperties properties)
			throws JMSException
	{

//		initSession( transacted,acknowledgeMode);
		MessageProducer producer = null;
		try
		{
			Destination destination = null;
//			if (destinationType == MQUtil.TYPE_QUEUE)
//			{
//				destination = session.createQueue(destination_);
//			}
//			else
//			{
//				destination = session.createTopic(destination_);
//			}
			
			LOG.debug("send message to " + destination_
                    + " build destination");
          destination = session.createDestination( destination_, destinationType);
          LOG.debug("send message to " + destination_
                    + " build destination end.");

			int deliveryMode = persistent ? DeliveryMode.PERSISTENT
					: DeliveryMode.NON_PERSISTENT;
			producer = session.createProducer(destination);
			if(properties != null)
				MQUtil.initMessage(message, properties);
			producer.send(message, deliveryMode, priority, timeToLive);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Sent! to destination: " + destination + " message: "
						+ message);
			}
			message = null;
		}
		catch (JMSException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JMSException(e.getMessage());
		}
		finally
		{
			if (producer != null)
				try
				{
					producer.close();
				}
				catch (Exception e)
				{

				}

		}
	}

	public void send(InputStream in,int destinationType, String destination_, Message message,
					 boolean persistent, int priority, long timeToLive,  JMSProperties properties, Logger log)
			throws JMSException
	{



		byte[] send = null;
		BytesMessage msg  = null;

		/* 是否加密 */
		if (properties.isEncrypt())
		{
			try
			{
				send = MQUtil.readTxtFileByte(in);
			}
			catch (Exception e)
			{
				throw new JMSException(e.getMessage());
			}
//			if (send == null || send.length == 0)
//				return;
			// String name = file.getName();

			if (send != null )
			{
				EncryptDecryptAlgo ed = new EncryptDecryptAlgo();
				send = ed.encrypt(send);
			}
			msg = session.createBytesMessage(send);
			if(properties != null)
				MQUtil.initMessage(msg, properties);

			send = null;
		}
		else
		{
			msg = session.createBytesMessage();
			if(properties != null)
				MQUtil.initMessage(msg, properties);
			if (!MQUtil.readTxtFileByte(msg, in))
//				return;
				;

		}
		send(  destinationType,   destination_,   msg,
				  persistent,   priority,   timeToLive, (JMSProperties)null);
	}



	

  
    
    

}
