package org.frameworkset.mq;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

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
}
