package org.gradle;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Invoke {
/*	private static final Logger servicelog = LoggerFactory.getLogger("servicelog");
	private static final Logger weblog = LoggerFactory.getLogger("weblog");*/
	
	private static final Logger log_invoke = LoggerFactory.getLogger(Invoke.class);
//	private static final Logger log_httpclientutils = LoggerFactory.getLogger(HttpClientUtils.class);

//	
	@Test
    public  void log() {
    	long interval = 0;
    	while(true) {
		
			log_invoke.info("日志信息");
		
			if(interval > 0) {
				try {
					Thread.currentThread().sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
    }	
}

