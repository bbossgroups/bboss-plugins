package org.frameworkset.mq;

import javax.jms.*;

/**
 * Created by 1 on 2017/5/26.
 */
public interface MessageSession {
    public int getDeliveryMode(boolean persistent);
    public BytesMessage createBytesMessage(byte[] data,JMSProperties properties) throws JMSException;

    public BytesMessage createBytesMessage(byte[] data) throws JMSException;



    public ObjectMessage createObjectMessage(java.io.Serializable object, JMSProperties properties) throws JMSException;

    public ObjectMessage createObjectMessage(java.io.Serializable object) throws JMSException;



    public TextMessage createTextMessage(String msg, JMSProperties properties) throws JMSException;

    public TextMessage createTextMessage(String msg) throws JMSException;
    public javax.jms.Destination createDestination(String destination, int destinationType) throws JMSException;
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException;
    public TopicSubscriber createDurableSubscriber(Topic destination, String name, String selector, boolean noLocal) throws JMSException;
    public Topic createTopic(String destination) throws JMSException;
    public int getAcknowledgeMode()throws JMSException;
    public void unsubscribe(String unsubscribename )throws JMSException;
    public MessageProducer createProducer(Destination destination)throws JMSException;
    
    public MessageProducer createProducer(String destination, int destinationType)throws JMSException;
    public BytesMessage createBytesMessage() throws JMSException;
    
   
}
