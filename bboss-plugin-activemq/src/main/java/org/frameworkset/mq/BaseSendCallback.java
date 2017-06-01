package org.frameworkset.mq;

import javax.jms.Session;

public abstract class BaseSendCallback implements SendCallback {


	@Override
	public boolean autocommit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int ackMode() {
		// TODO Auto-generated method stub
		return Session.AUTO_ACKNOWLEDGE;
	}

}
