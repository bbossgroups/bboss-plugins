package org.frameworkset.mq;

import javax.jms.MessageProducer;

/**
 * Created by 1 on 2017/5/26.
 */
public interface SendCallback {
    public abstract void sendMessage(MessageSession session,MessageProducer producer);
    public boolean autocommit();
    /**
     * Session.AUTO_ACKNOWLEDGE
     *  int AUTO_ACKNOWLEDGE = 1;
        int CLIENT_ACKNOWLEDGE = 2;
       int DUPS_OK_ACKNOWLEDGE = 3;
     */
    public int ackMode();

}
