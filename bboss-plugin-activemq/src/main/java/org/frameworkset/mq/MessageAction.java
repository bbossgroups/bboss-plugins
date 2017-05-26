package org.frameworkset.mq;

import javax.jms.*;

/**
 * Created by 1 on 2017/5/26.
 */
public abstract class MessageAction implements SendCallback,MessageSession{
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


    public abstract void sendMessage(MessageSession session,MessageProducer producer);

    public void commit() throws JMSException {
        this.assertStarted();
        this.session.commit();
    }
    public void rollback() throws JMSException {
        this.assertStarted();
        this.session.rollback();
    }


}
