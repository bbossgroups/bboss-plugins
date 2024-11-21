package org.frameworkset.rocketmq;
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
public abstract class RocketMQConsumersStarter {
	private static Logger logger = LoggerFactory.getLogger(RocketMQConsumersStarter.class);

    private static Map<String, List<RocketMQListener>> iocRocketmqListeners = new LinkedHashMap<>();
    private static Object lock = new Object();
    /**
     * 销毁ioc配置对应的容器中管理的Rocketmq消费程序
     * @param applicationContextIOC
     */
    public static void shutdownConsumers(String applicationContextIOC){
        synchronized (lock) {
            List<RocketMQListener> rocketMQListeners = iocRocketmqListeners.get(applicationContextIOC);
            if (rocketMQListeners != null && rocketMQListeners.size() > 0) {
                for (RocketMQListener rocketMQListener : rocketMQListeners) {
                    rocketMQListener.shutdown();
                }
                rocketMQListeners.clear();
                iocRocketmqListeners.remove(applicationContextIOC);
            }
        }
    }
    /**
     * 销毁所有容器中管理的Rocketmq消费程序
     */
    public static void shutdownAllConsumers(){
        synchronized (lock) {
            Iterator<Map.Entry<String, List<RocketMQListener>>> iterator = iocRocketmqListeners.entrySet().iterator();
            while (iterator.hasNext()) {
                List<RocketMQListener> rocketMQListeners = iterator.next().getValue();
                if (rocketMQListeners != null && rocketMQListeners.size() > 0) {
                    for (RocketMQListener rocketMQListener : rocketMQListeners) {
                        rocketMQListener.shutdown();
                    }
                }
            }
            iocRocketmqListeners.clear();
        }
    }
    /**
     * 启动ioc配置对应的容器中管理的Rocketmq消费程序，自动注册消费程序销毁hook，以便在jvm退出时自动关闭消费程序
     * @param applicationContextIOC
     */
    public static void startConsumers(String applicationContextIOC){
        startConsumers( applicationContextIOC,false);


	}

    /**
     * 启动ioc配置对应的容器中管理的Rocketmq消费程序，通过addShutdownHook控制是否注册消费程序销毁hook，以便在jvm退出时自动关闭消费程序 true 注册，false不注册
     * false 情况下需要手动调用shutdownConsumers(String applicationContextIOC)方法或者shutdownAllConsumers()方法销毁对应的消费程序
     * @param applicationContextIOC
     * @param addShutdownHook
     */
    public static void startConsumers(String applicationContextIOC,boolean addShutdownHook){
        List<RocketMQListener> rocketMQListeners_ = iocRocketmqListeners.get(applicationContextIOC);
        if(rocketMQListeners_ != null){
            logger.info("Rocketmq Consumers in ioc {}, addShutdownHook {} 已经启动，忽略本次操作，",applicationContextIOC, addShutdownHook);
        }
        BaseApplicationContext context = DefaultApplicationContext.getApplicationContext(applicationContextIOC);
        synchronized (lock) {
            rocketMQListeners_ = iocRocketmqListeners.get(applicationContextIOC);
            if(rocketMQListeners_ != null){
                logger.info("Rocketmq Consumers in ioc {}, addShutdownHook {} 已经启动，忽略本次操作，",applicationContextIOC, addShutdownHook);
            }
            context.start(new Starter() {
                @Override
                public void start(Pro pro, BaseApplicationContext ioc) {
                    Object _service = ioc.getBeanObject(pro.getName());
                    if (_service == null)
                        return;
                    List<RocketMQListener> rocketMQListeners = iocRocketmqListeners.get(applicationContextIOC);
                    if (rocketMQListeners == null) {
                        rocketMQListeners = new ArrayList<>();
                        iocRocketmqListeners.put(applicationContextIOC, rocketMQListeners);
                    }

                    if (_service instanceof RocketMQListener) {
                        RocketMQListener consumer = (RocketMQListener) _service;
//					Thread t = new Thread(consumer,"Rocketmq-consumer-"+pro.getName());
//					t.start();
                        consumer.run(addShutdownHook);
                        rocketMQListeners.add(consumer);
                        if (logger.isInfoEnabled()) {
                            logger.info("Rocketmq Listener[name:{},class:{}] started.", pro.getName(), pro.getClazz());
                        }
                    }
                }

                @Override
                public void failed(Pro bean, BaseApplicationContext baseApplicationContext, Throwable e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(new StringBuilder().append("Rocketmq Listener[name:").append(bean.getName())
                                        .append(",class:").append(bean.getClazz())
                                        .append("] start failed:").toString(),
                                e);

                    }
                }
            });
        }


    }
}
