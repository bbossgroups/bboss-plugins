package org.frameworkset.rocketmq;



import com.frameworkset.util.SimpleStringUtil;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.frameworkset.rocketmq.codec.CodecDeserial;
import org.frameworkset.rocketmq.codec.RocketmqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BaseRocketMQConsumerThread extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(BaseRocketMQConsumerThread.class);

    private String accessKey;
    private String secretKey;
    private String securityToken;
    private String signature;
 

    /**
     * namesrv地址
     */
    private String namesrvAddr ;
    
    
    private String consumeFromWhere;
    /**
     * 单位到秒
     * 20191024171201
     */
    private String consumeTimestamp;
    private Boolean enableSsl ;
    private String tag  ;
//    private Long awaitDuration = 10000l;

    private String consumerGroup;
    
	protected StoreService storeService;
	protected String workThreadname;
	protected  boolean shutdown ;
	protected BaseRocketMQConsumer consumer;
	private DefaultMQPushConsumer defaultMQPushConsumer;


    protected String[] topics;


    protected CodecDeserial keyCodecDeserial;
    protected CodecDeserial valueCodecDeserial;
	protected Integer maxPollRecords;
    protected Integer consumeMessageBatchMaxSize;
	protected Integer workThreads =20;
    /**
     * 队列编号
     */
	protected int partition;
 

    public void setValueCodecDeserial(CodecDeserial valueCodecDeserial) {
        this.valueCodecDeserial = valueCodecDeserial;
    }

    public Integer getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }
    public CodecDeserial getKeyCodecDeserial() {
        return keyCodecDeserial;
    }

    public void setKeyCodecDeserial(CodecDeserial keyCodecDeserial) {
        this.keyCodecDeserial = keyCodecDeserial;
    }

    public void setConsumeMessageBatchMaxSize(Integer consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }
   
	public BaseRocketMQConsumerThread(int partition, BaseRocketMQConsumer consumer, String[] topics, StoreService storeService) {
		super("RocketmqBatchConsumer-"+topicsStr(topics)+"-p"+partition);
		workThreadname = this.getName() + "-work";
		this.storeService = storeService;
		this.partition = partition;
		this.consumer = consumer;
		this.topics = topics;


	}

    public String getConsumeFromWhere() {
        return consumeFromWhere;
    }

    public void setConsumeFromWhere(String consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }

    private static String topicsStr(String[] topics){
		StringBuilder builder = new StringBuilder();
		boolean s = false;
		for(String topic:topics) {
			if(!s) {
				builder.append(topic);
				s = true;
			}
			else{
				builder.append(",").append(topic);
			}
		}
		return builder.toString();
	}
 

	public void setWorkThreads(Integer workThreads) {
		this.workThreads = workThreads;
	}


	public Integer getWorkThreads() {
		return workThreads;
	}
 


	public void setMaxPollRecords(Integer maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	public Integer getMaxPollRecords() {
		return maxPollRecords;
	}

 

 
	private boolean consumerClosed;
	private void closeConsumer(){
		synchronized (this){
			if(consumerClosed)
				return;
			consumerClosed = true;
		}
		try {
			if (this.defaultMQPushConsumer != null) {
                defaultMQPushConsumer.shutdown();
				logger.info("simpleConsumer[{}] closed topic:{}",workThreadname,topics);
			}
		}
		catch (Exception e){
//			consumerClosed = false;
			logger.warn("simpleConsumer["+workThreadname+"] closed topic:"+topics,e);
		}

	}

	public void shutdown(){
		synchronized (this) {
			if (shutdown)
				return;
			this.shutdown = true;

		}
 

        closeConsumer();
        try {
            this.join();

		}
		catch (Exception e){

		}
		 

	}
	private synchronized void buildConsumerAndSubscribe()   {
        if(shutdown)
            return;
//		Map<String,Object> properties = consumer.getConsumerPropes();

//		if(workThreads != null){
//			executor = ThreadPoolFactory.buildThreadPool(workThreadname,discardRejectMessage == null?"Rocketmq consumer message handle":discardRejectMessage,
//					workThreads,workQueue,blockedWaitTimeout,warnMultsRejects,true,false);
//		}
        DefaultMQPushConsumer consumer = null;
        try {
            /*
             * Instantiate with specified consumer group name.
             */
            //消费者
            AclClientRPCHook auth = null;
            if(SimpleStringUtil.isNotEmpty(accessKey)) {
                SessionCredentials sessionCredentials = new SessionCredentials(accessKey, secretKey, securityToken);
                sessionCredentials.setSignature(signature);
                auth = new AclClientRPCHook(sessionCredentials);
            }
            consumer = new DefaultMQPushConsumer(this.consumerGroup,auth);
            if(enableSsl != null)
                consumer.setUseTLS(enableSsl);
            consumer.setConsumeThreadMax(workThreads);
            consumer.setConsumeThreadMin(workThreads);
            
            
//            consumer.setPullInterval(pollTimeout);
            consumer.setPullBatchSize(maxPollRecords);

            consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);

            /*
             * Specify name server addresses.
             * <p/>
             *
             * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
             * <pre>
             * {@code
             * consumer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
             * }
             * </pre>
             */
            // Uncomment the following line while debugging, namesrvAddr should be set to your local address
            consumer.setNamesrvAddr(this.getNamesrvAddr());

            /*
             * Specify where to start in case the specific consumer group is a brand-new one.
             */
            consumer.setConsumeFromWhere(convertConsumeFromWhere(consumer, consumeFromWhere));
            
            /*
             * Subscribe one more topic to consume.
             */
            if(SimpleStringUtil.isNotEmpty(tag)) {
                consumer.subscribe(topics[0], tag);
            }
            else{
                consumer.subscribe(topics[0], "*");
            }

            /*
             *  Register callback to execute on arrival of messages fetched from brokers.
             */
            consumer.registerMessageListener((MessageListenerConcurrently) (msg, context) -> {
//                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msg);
                try {
                    doHandle(msg);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                catch (RockemqException rockemqException){
                    logger.error("",rockemqException);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                
            });
           
            /*
             *  Launch the consumer instance.
             */
            consumer.start();
            this.defaultMQPushConsumer = consumer;
        }
        catch (Exception e){
            if(consumer != null){
                try{
                    consumer.shutdown();
                }
                catch (Exception ex){
                    
                }
            }
            throw new RockemqException(e);
        }
        
       
	}
    
    private ConsumeFromWhere convertConsumeFromWhere(DefaultMQPushConsumer consumer,String consumeFromWhere){
        if(consumeFromWhere  == null){
            return null;
        }
        if(consumeFromWhere.equals("CONSUME_FROM_LAST_OFFSET")){
            return ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
        }
        else if(consumeFromWhere.equals("CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST")){
            return ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST;
        }
        else if(consumeFromWhere.equals("CONSUME_FROM_MIN_OFFSET")){
            return ConsumeFromWhere.CONSUME_FROM_MIN_OFFSET;
        }
        else if(consumeFromWhere.equals("CONSUME_FROM_MAX_OFFSET")){
            return ConsumeFromWhere.CONSUME_FROM_MAX_OFFSET;
        }
        else if(consumeFromWhere.equals("CONSUME_FROM_FIRST_OFFSET")){
            return ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
        }
        else if(consumeFromWhere.equals("CONSUME_FROM_TIMESTAMP")){
            //defaultLitePullConsumer.setConsumeTimestamp("20191024171201");
            consumer.setConsumeTimestamp(consumeTimestamp);
            return ConsumeFromWhere.CONSUME_FROM_TIMESTAMP;
        } 
        else {
            logger.warn("Unknown consumeFromWhere: {}",consumeFromWhere);
        }
        return null;
    }

	@Override
	public void run() {
        buildConsumerAndSubscribe();
	}
	private void doHandle(List<MessageExt>  records){
		try {
            List<RocketmqMessage> messages = new ArrayList<>(records.size());
            for(MessageExt messageExt:records){
                messages.add(valueCodecDeserial.deserial(messageExt));
            }
			storeService.store(messages);             
		}
		 
        catch (RockemqException e) {
            throw e;
        }
		catch (Exception e) {
			throw new RockemqException(e);
		} catch (Throwable e) {
            throw new RockemqException(e);
		}
	}
 

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
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
 
    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public void setConsumeTimestamp(String consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }
}
