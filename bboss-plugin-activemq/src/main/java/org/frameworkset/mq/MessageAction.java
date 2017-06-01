package org.frameworkset.mq;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 1 on 2017/5/26.
 */
public abstract class MessageAction implements SendCallback,MessageSession{
	protected static final Logger logger = LoggerFactory.getLogger(MessageAction.class);
    protected Session session;
    protected SendCallback callback;
    public MessageAction(Session session,SendCallback callback){
        this.session = session;
        this.callback = callback;
    }

    public BytesMessage createBytesMessage(byte[] data,JMSProperties properties) throws JMSException
    {

        this.assertStarted();
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(data);
        if(properties != null)
            MQUtil.initMessage(message, properties);
        return message;

    }

    public BytesMessage createBytesMessage(byte[] data) throws JMSException
    {

        this.assertStarted();
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(data);

        return message;

    }

    public BytesMessage createBytesMessage() throws JMSException
    {

        this.assertStarted();
        BytesMessage message = session.createBytesMessage();

        return message;

    }


    protected void assertStarted() throws JMSException
    {

        if (this.session == null)
        {
            throw new JMSException("MQClient has not been started.");
        }

    }

    public ObjectMessage createObjectMessage(java.io.Serializable object,JMSProperties properties)
            throws JMSException
    {

        this.assertStarted();
        ObjectMessage message = session.createObjectMessage(object);
        if(properties != null)
            MQUtil.initMessage(message, properties);
        return message;

    }

    public int getDeliveryMode(boolean persistent){
        int deliveryMode = persistent ? DeliveryMode.PERSISTENT
                : DeliveryMode.NON_PERSISTENT;
        return deliveryMode;
    }
    public ObjectMessage createObjectMessage(java.io.Serializable object)
            throws JMSException
    {

        this.assertStarted();
        ObjectMessage message = session.createObjectMessage(object);

        return message;

    }



    public TextMessage createTextMessage(String msg,JMSProperties properties) throws JMSException
    {

        this.assertStarted();
        TextMessage message = session.createTextMessage(msg);
        if(properties != null)
            MQUtil.initMessage(message, properties);
        return message;

    }

    public TextMessage createTextMessage(String msg) throws JMSException
    {

        this.assertStarted();
        TextMessage message = session.createTextMessage(msg);

        return message;

    }



    public void commit() throws JMSException {
        this.assertStarted();
        if(!this.autocommit())
        	this.session.commit();
    }
    public void rollback() throws JMSException {
        this.assertStarted();
        if(!this.autocommit())
        	this.session.rollback();
    }
    
    @Override
    public boolean autocommit() {
        return callback.autocommit();
    }

    @Override
    public int ackMode() {
        return callback.ackMode();
    }

	 

	@Override
	public Destination createDestination(String destination, int destinationType) throws JMSException {
		boolean isqueue = destinationType == MQUtil.TYPE_QUEUE;
		Destination destination_ = null;
		if (isqueue)
		{
		    if(logger .isDebugEnabled())
		    	logger.debug("send message to {}  QUEUE destination", destination
						 );
		      destination_ = session.createQueue(destination);
			 
		}
		else
		{
			 if(logger .isDebugEnabled())
			    	logger.debug("send message to {}  topic destination", destination);
			destination_ = session.createTopic(destination);
			 
		}
		return destination_;
	}

	@Override
	public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal)
			throws JMSException {
		// TODO Auto-generated method stub
		return this.session.createConsumer(destination, messageSelector, noLocal);
	}

	@Override
	public TopicSubscriber createDurableSubscriber(Topic destination, String name, String selector, boolean noLocal)
			throws JMSException {
		// TODO Auto-generated method stub
		return this.session.createDurableSubscriber(destination, name, selector, noLocal);
	}

	@Override
	public Topic createTopic(String destination) throws JMSException {
		// TODO Auto-generated method stub
		return this.session.createTopic(destination);
	}

	@Override
	public int getAcknowledgeMode() throws JMSException {
		// TODO Auto-generated method stub
		return this.session.getAcknowledgeMode();
	}

	@Override
	public void unsubscribe(String unsubscribename) throws JMSException {
		this.session.unsubscribe(unsubscribename);
		
	}

	@Override
	public MessageProducer createProducer(Destination destination) throws JMSException {
		// TODO Auto-generated method stub
		return this.session.createProducer(destination);
	}

	@Override
	public MessageProducer createProducer(String destination, int destinationType) throws JMSException {
		// TODO Auto-generated method stub
		Destination destination_ = this.createDestination(destination, destinationType);
		return this.session.createProducer(destination_);
	}

//	@Override
//	public void close() throws JMSException {
//		this.session.close();
//		
//	}


}
