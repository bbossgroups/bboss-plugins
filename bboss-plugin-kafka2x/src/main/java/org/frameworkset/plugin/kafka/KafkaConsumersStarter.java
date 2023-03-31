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

import java.util.*;

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
    private static Map<String, List<KafkaListener>> iocKafkaListeners = new LinkedHashMap<>();

    /**
     * 销毁ioc配置对应的容器中管理的kafka消费程序
     * @param applicationContextIOC
     */
    public static void shutdownConsumers(String applicationContextIOC){
        List<KafkaListener> kafkaListeners = iocKafkaListeners.get(applicationContextIOC);
        if(kafkaListeners != null && kafkaListeners.size() > 0) {
            for(KafkaListener kafkaListener: kafkaListeners){
                kafkaListener.shutdown();
            }
        }
    }
    /**
     * 销毁所有容器中管理的kafka消费程序
     */
    public static void shutdownAllConsumers(){
        Iterator<Map.Entry<String, List<KafkaListener>>> iterator = iocKafkaListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            List<KafkaListener> kafkaListeners = iterator.next().getValue();
            if (kafkaListeners != null && kafkaListeners.size() > 0) {
                for (KafkaListener kafkaListener : kafkaListeners) {
                    kafkaListener.shutdown();
                }
            }
        }
    }
    /**
     * 启动ioc配置对应的容器中管理的kafka消费程序，自动注册消费程序销毁hook，以便在jvm退出时自动关闭消费程序
     * @param applicationContextIOC
     */
    public static void startConsumers(String applicationContextIOC){
        startConsumers( applicationContextIOC,true);


	}

    /**
     * 启动ioc配置对应的容器中管理的kafka消费程序，通过addShutdownHook控制是否注册消费程序销毁hook，以便在jvm退出时自动关闭消费程序 true 注册，false不注册
     * false 情况下需要手动调用shutdownConsumers(String applicationContextIOC)方法或者shutdownAllConsumers()方法销毁对应的消费程序
     * @param applicationContextIOC
     * @param addShutdownHook
     */
    public static void startConsumers(String applicationContextIOC,boolean addShutdownHook){
        BaseApplicationContext context = DefaultApplicationContext.getApplicationContext(applicationContextIOC);
        context.start(new Starter() {
            @Override
            public void start(Pro pro, BaseApplicationContext ioc) {
                Object _service = ioc.getBeanObject(pro.getName());
                if(_service == null)
                    return;
                List<KafkaListener> kafkaListeners = iocKafkaListeners.get(applicationContextIOC);
                if(kafkaListeners == null) {
                    kafkaListeners = new ArrayList<>();
                    iocKafkaListeners.put(applicationContextIOC,kafkaListeners);
                }

                if(_service instanceof KafkaListener){
                    KafkaListener consumer = (KafkaListener)_service;
//					Thread t = new Thread(consumer,"kafka-consumer-"+pro.getName());
//					t.start();
                    consumer.run(addShutdownHook);
                    kafkaListeners.add(consumer);
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
