package org.frameworkset.mq;

import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Created by 1 on 2017/5/26.
 */
public class DefaultMessageAction extends MessageAction {
    public DefaultMessageAction(Session session, SendCallback callback) {
        super(session, callback);
    }

    @Override
    public void sendMessage(MessageSession session, MessageProducer producer) {
        callback.sendMessage(session,producer);
    }

    @Override
    public boolean autocommit() {
        return callback.autocommit();
    }

    @Override
    public int ackMode() {
        return callback.ackMode();
    }
}
