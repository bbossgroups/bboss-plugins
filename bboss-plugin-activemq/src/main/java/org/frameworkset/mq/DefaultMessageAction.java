package org.frameworkset.mq;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by 1 on 2017/5/26.
 */
public class DefaultMessageAction extends MessageAction {
    public DefaultMessageAction(Session session, SendCallback callback) {
        super(session, callback);
    }
    
    public DefaultMessageAction(Session session) {
        super(session, null);
    }

    @Override
    public void sendMessage(MessageSession session) throws JMSException {
        callback.sendMessage(session);
    }
    
    
    public void sendMessage() throws JMSException {
        callback.sendMessage(this);
    }

   
}
