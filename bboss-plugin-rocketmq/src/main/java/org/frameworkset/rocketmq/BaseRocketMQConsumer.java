package org.frameworkset.rocketmq;

import org.frameworkset.rocketmq.codec.CodecDeserial;
import org.frameworkset.rocketmq.codec.BytesCodecDeserial;
import org.frameworkset.rocketmq.codec.RocketmqCodecUtil;
import org.frameworkset.spi.support.ApplicationObjectSupport;
import org.frameworkset.util.shutdown.ShutdownUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

//import kafka.consumer.ConsumerConfig;
//
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;

public class BaseRocketMQConsumer extends ApplicationObjectSupport implements RocketMQListener {
//	private BaseApplicationContext applicationContext;
	protected List<BaseRocketMQConsumerThread> baseRocketMQConsumerThreadList = new ArrayList<>();
	protected String topic;

    protected String accessKey;
    protected String secretKey;
    private String securityToken;
    private String signature;
    protected String endpoints ;
    protected Boolean enableSsl ;
    protected String tag  ;
//    protected long awaitDuration = 10000l;
    
    private String consumerGroup;

    /**
     * namesrv地址
     */
    private String namesrvAddr ;


    /**
     * CONSUME_FROM_LAST_OFFSET,
     *
     *     @Deprecated
     *     CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST,
     *     @Deprecated
     *     CONSUME_FROM_MIN_OFFSET,
     *     @Deprecated
     *     CONSUME_FROM_MAX_OFFSET,
     *     CONSUME_FROM_FIRST_OFFSET,
     *     CONSUME_FROM_TIMESTAMP,
     */
    private String consumeFromWhere;


    /**
     * 单位到秒
     * 20191024171201
     */
    private String consumeTimestamp;
    protected  volatile boolean shutdown ;
    private Object shutdownlock = new Object();

	//	private String zookeeperConnect;
	protected Map<String,Object> consumerPropes;
	private boolean autoCommit;
 
    private int oldThreads ;
//	protected ExecutorService executor;
    /**
     * 保留定义
     */
	protected String keyDeserializer;
    /**
     * 对消息内容进行反序列化
     */
	protected String valueDeserializer;

    /**
     * 从rocketmq一次性拉取的最大记录数据
     */
    protected Integer maxPollRecords;


    /**
     * 工作线程分批处理的记录数，默认和maxPollRecords一致
     */
    protected Integer consumeMessageBatchMaxSize;
	protected Integer workThreads ;
    private Object lock = new Object();
	

	protected Integer workQueue = 100;
 
  
 
    public Integer getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(Integer consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }
	public String getTopic() {
		return topic;
	}
 

	public void setWorkQueue(Integer workQueue) {
		this.workQueue = workQueue;
	}

	public void setWorkThreads(Integer workThreads) {
		this.workThreads = workThreads;
	}

	public Integer getWorkQueue() {
		return workQueue;
	}

	public Integer getWorkThreads() {
		return workThreads;
	}

	public void setKeyDeserializer(String keyDeserializer) {
		this.keyDeserializer = keyDeserializer;
	}

	public String getKeyDeserializer() {
		return keyDeserializer;
	}

	public void setValueDeserializer(String valueDeserializer) {
		this.valueDeserializer = valueDeserializer;
	}

	public String getValueDeserializer() {
		return valueDeserializer;
	}



 
	public void setMaxPollRecords(Integer maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	public Integer getMaxPollRecords() {
		return maxPollRecords;
	}
//	public KafkaConsumer getConsumer() {
//		return consumer;
//	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

 



	public void setConsumerPropes(Map<String,Object> consumerPropes) {
		this.consumerPropes = consumerPropes;
	}



    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getConsumeFromWhere() {
        return consumeFromWhere;
    }

    /**
     * CONSUME_FROM_LAST_OFFSET,
     *
     *     @Deprecated
     *     CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST,
     *     @Deprecated
     *     CONSUME_FROM_MIN_OFFSET,
     *     @Deprecated
     *     CONSUME_FROM_MAX_OFFSET,
     *     CONSUME_FROM_FIRST_OFFSET,
     *     CONSUME_FROM_TIMESTAMP,
     */
    public void setConsumeFromWhere(String consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }

    public void shutdown(){
		synchronized (shutdownlock) {
			if(shutdown)
				return;
			shutdown = true;
		}

        synchronized (lock) {
            if (baseRocketMQConsumerThreadList.size() > 0) {
                for (BaseRocketMQConsumerThread baseRocketMQConsumerThread : baseRocketMQConsumerThreadList) {
                    baseRocketMQConsumerThread.shutdown();
                }
            }
        }
//		if(executor != null)
//			executor.shutdown();
//		if(consumer != null)
//			consumer.close();
//		if(storeService != null)
//			storeService.closeService();
	}

	public boolean isShutdown(){
        synchronized (shutdownlock) {
            return shutdown;
        }
	}

//	String topic,String zookeeperConnect, HDFSService logstashService
	
	public BaseRocketMQConsumer() {
//		this.topic = topic;
//		props = new Properties();    
//		this.zookeeperConnect = zookeeperConnect;
//		props.put("zookeeper.connect", this.zookeeperConnect); 
//		this.logstashService = logstashService;
//        
////		props.put("zookeeper.connect", "localhost:2181");    
////        props.put("zookeeper.connectiontimeout.ms", "30000");    
//        props.put("group.id","logstash");  
//        props.put("zookeeper.session.timeout.ms", "1000");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.commit.enable", true);enable.auto.commit
//        
//        props.put("auto.commit.interval.ms", "1000"); 
//        props.put("auto.offset.reset", "smallest");
//        props.put("application.id", "logstash_app");
//        
//        props.put("value.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
//		props.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
       
	}
	public void init(){
	}
	protected StoreService storeService = null;

    public void run(boolean addShutdownHook){
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        final String[] topics = topic.split("\\,");
//	    for(String t:topics)
//	    {
//	    	String[] infos = t.split(":");
//	    	topicCountMap.put(infos[0], new Integer(a_numThreads));
//	    }
//		executor = Executors.newFixedThreadPool(a_numThreads,new ThreadFactory(){
//			private int i = 0;
//			@Override
//			public Thread newThread(Runnable r) {
//
//				return new Thread(r,"BaseKafkaConsumer-"+(i ++));
//			}
//		});
        synchronized (lock) {
                BaseRocketMQConsumerThread runnable = buildRunnable(0, topics);
                baseRocketMQConsumerThreadList.add(runnable);
                runnable.run();

        }

        if(addShutdownHook) {
            ShutdownUtil.addShutdownHook(new Runnable() {
                @Override
                public void run() {
                    shutdown();
                }
            });
        }
    }

	@Override
	public void run() {

        run(true);

	}
	
     
	protected BaseRocketMQConsumerThread buildRunnable(int partition, String[] topic){
		BaseRocketMQConsumerThread baseRocketMQConsumerThread = new BaseRocketMQConsumerThread(partition,this,topic,storeService);
		if(keyDeserializer != null) {
            baseRocketMQConsumerThread.setKeyCodecDeserial(RocketmqCodecUtil.convertCodecDeserial(keyDeserializer, true,consumerPropes));
        }
        CodecDeserial codecDeserial = RocketmqCodecUtil.convertCodecDeserial(valueDeserializer, true,consumerPropes);
        if(codecDeserial == null){            
            codecDeserial = new BytesCodecDeserial();
        }
		baseRocketMQConsumerThread.setValueCodecDeserial(codecDeserial);
		baseRocketMQConsumerThread.setMaxPollRecords(maxPollRecords);
		baseRocketMQConsumerThread.setWorkThreads(workThreads);
        baseRocketMQConsumerThread.setConsumerGroup(this.consumerGroup);
        baseRocketMQConsumerThread.setAccessKey(accessKey);
        baseRocketMQConsumerThread.setSecretKey(secretKey);
        baseRocketMQConsumerThread.setSignature(signature);
        baseRocketMQConsumerThread.setSecurityToken(securityToken);
        baseRocketMQConsumerThread.setTag(tag);
        baseRocketMQConsumerThread.setEnableSsl(enableSsl);
        baseRocketMQConsumerThread.setNamesrvAddr(this.namesrvAddr);
        baseRocketMQConsumerThread.setConsumeFromWhere(consumeFromWhere);
        baseRocketMQConsumerThread.setConsumeTimestamp(consumeTimestamp);
        baseRocketMQConsumerThread.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize == null?maxPollRecords:consumeMessageBatchMaxSize);
        
		return baseRocketMQConsumerThread;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}


    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String endpoints) {
        this.endpoints = endpoints;
    }

    public Boolean getEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(Boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

//    public long getAwaitDuration() {
//        return awaitDuration;
//    }
//
//    public void setAwaitDuration(long awaitDuration) {
//        this.awaitDuration = awaitDuration;
//    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Map<String,Object> getConsumerPropes() {
        return consumerPropes;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getConsumeTimestamp() {
        return consumeTimestamp;
    }

    public void setConsumeTimestamp(String consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }

}
