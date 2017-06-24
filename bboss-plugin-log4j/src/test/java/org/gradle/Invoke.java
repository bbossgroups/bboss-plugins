package org.gradle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Invoke {
/*	private static final Logger servicelog = LoggerFactory.getLogger("servicelog");
	private static final Logger weblog = LoggerFactory.getLogger("weblog");*/
	
	private static final Logger log_invoke = LoggerFactory.getLogger(Invoke.class);
//	private static final Logger log_httpclientutils = LoggerFactory.getLogger(HttpClientUtils.class);

//	
    public static void main(String[] args) {
    	long interval = 0;
    	while(true) {
		//	System.out.println("aaa");
		//	servicelog.info(servicelogstr);
		//	weblog.info(weblogstr);
			log_invoke.info("日志信息");
		//	log_httpclientutils.info("httpclientutils日志信息");
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

