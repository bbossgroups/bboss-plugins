package org.frameworkset.plugin.kafka;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.frameworkset.spi.assemble.Pro;
import org.frameworkset.spi.runtime.Starter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/9/28 8:16
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class KafkaConsumersStarter {
	private static Logger logger = LoggerFactory.getLogger(KafkaConsumersStarter.class);
	public static void startConsumers(String applicationContextIOC){
		BaseApplicationContext context = DefaultApplicationContext.getApplicationContext(applicationContextIOC);
		context.start(new Starter() {
			@Override
			public void start(Pro pro, BaseApplicationContext ioc) {
				Object _service = ioc.getBeanObject(pro.getName());
				if(_service == null)
					return;
				if(_service instanceof KafkaListener){
					KafkaListener consumer = (KafkaListener)_service;
					Thread t = new Thread(consumer,"kafka-consumer-"+pro.getName());
					t.start();
					if(logger.isInfoEnabled()){
						logger.info("Kafka Listener[name:{},class:{}] started.",pro.getName(),pro.getClazz());
					}
				}
			}

			@Override
			public void failed(Pro bean, BaseApplicationContext baseApplicationContext,Throwable e) {
				if (logger.isErrorEnabled())
				{
					logger.error(new StringBuilder().append("Kafka Listener[name:").append(bean.getName())
									.append(",class:").append(bean.getClazz())
									.append("] start failed:").toString(),
							e);

				}
			}
		});


	}
}
