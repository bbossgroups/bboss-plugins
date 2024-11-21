//package org.frameworkset.rocketmq;
//
//
//import org.apache.rocketmq.client.apis.*;
//import org.apache.rocketmq.client.apis.consumer.FilterExpression;
//import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
//import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
//import org.apache.rocketmq.client.apis.message.MessageView;
//import org.frameworkset.util.concurrent.ThreadPoolFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.Duration;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//
//public class SimpleRocketMQConsumerThread extends Thread {
//	private static final Logger logger = LoggerFactory.getLogger(SimpleRocketMQConsumerThread.class);
//
//    private String accessKey;
//    private String secretKey;
//    private String endpoints ;
//    private Boolean enableSsl ;
//    private String tag  ;
//    private Long awaitDuration = 10000l;
//
//    private String consumerGroup;
//    
//	protected StoreService storeService;
//	protected String workThreadname;
//	protected  boolean shutdown ;
//	protected BaseRocketMQConsumer consumer;
//    protected boolean autoCommit = true;
//	private SimpleConsumer simpleConsumer;
//	protected  String[] topics;
//	protected long pollTimeout;
//	protected String keyDeserializer;
//	protected String valueDeserializer;
//	protected Integer maxPollRecords;
//	protected Integer workThreads ;
//	protected Integer workQueue = 100;
//	protected ExecutorService executor;
//    /**
//     * 队列编号
//     */
//	protected int partition;
//	protected String discardRejectMessage;
//
//    protected FilterExpression filterExpression;
//
//	public long getBlockedWaitTimeout() {
//		return blockedWaitTimeout;
//	}
//
//	public void setBlockedWaitTimeout(long blockedWaitTimeout) {
//		this.blockedWaitTimeout = blockedWaitTimeout;
//	}
//
//	protected long blockedWaitTimeout = -1l;
//
//	public int getWarnMultsRejects() {
//		return warnMultsRejects;
//	}
//
//	public void setWarnMultsRejects(int warnMultsRejects) {
//		this.warnMultsRejects = warnMultsRejects;
//	}
//
//	protected int warnMultsRejects = 500;
//	public String getDiscardRejectMessage() {
//		return discardRejectMessage;
//	}
//
//	public void setDiscardRejectMessage(String discardRejectMessage) {
//		this.discardRejectMessage = discardRejectMessage;
//	}
////	String topic,
////	private HDFSService logstashService;
////	protected ConsumerConnector consumer;
//	public SimpleRocketMQConsumerThread(int partition, BaseRocketMQConsumer consumer, String[] topics, StoreService storeService) {
//		super("RocketmqBatchConsumer-"+topicsStr(topics)+"-p"+partition);
//		workThreadname = this.getName() + "-work";
//		this.storeService = storeService;
//		this.partition = partition;
//		this.consumer = consumer;
//		this.topics = topics;
//
//
//	}
//	private static String topicsStr(String[] topics){
//		StringBuilder builder = new StringBuilder();
//		boolean s = false;
//		for(String topic:topics) {
//			if(!s) {
//				builder.append(topic);
//				s = true;
//			}
//			else{
//				builder.append(",").append(topic);
//			}
//		}
//		return builder.toString();
//	}
//	public void setPollTimeout(long pollTimeout) {
//		this.pollTimeout = pollTimeout;
//	}
//
//	public long getPollTimeout() {
//		return pollTimeout;
//	}
//
//	public void setWorkQueue(Integer workQueue) {
//		this.workQueue = workQueue;
//	}
//
//	public void setWorkThreads(Integer workThreads) {
//		this.workThreads = workThreads;
//	}
//
//	public Integer getWorkQueue() {
//		return workQueue;
//	}
//
//	public Integer getWorkThreads() {
//		return workThreads;
//	}
// 
//
//
//	public void setMaxPollRecords(Integer maxPollRecords) {
//		this.maxPollRecords = maxPollRecords;
//	}
//
//	public Integer getMaxPollRecords() {
//		return maxPollRecords;
//	}
//
//
//
//	public void setValueDeserializer(String valueDeserializer) {
//		this.valueDeserializer = valueDeserializer;
//	}
//
//	public String getValueDeserializer() {
//		return valueDeserializer;
//	}
//
//	public void setKeyDeserializer(String keyDeserializer) {
//		this.keyDeserializer = keyDeserializer;
//	}
//
//	public String getKeyDeserializer() {
//		return keyDeserializer;
//	}
//	private boolean consumerClosed;
//	private void closeConsumer(){
//		synchronized (this){
//			if(consumerClosed)
//				return;
//			consumerClosed = true;
//		}
//		try {
//			if (this.simpleConsumer != null) {
//                simpleConsumer.close();
//				logger.info("simpleConsumer[{}] closed topic:{}",workThreadname,topics);
//			}
//		}
//		catch (Exception e){
////			consumerClosed = false;
//			logger.warn("simpleConsumer["+workThreadname+"] closed topic:"+topics,e);
//		}
//
//	}
//
//	public void shutdown(){
//		synchronized (this) {
//			if (shutdown)
//				return;
//			this.shutdown = true;
//
//		}
// 
//
////            closeConsumer();
//        try {
//            this.join();
//
//		}
//		catch (Exception e){
//
//		}
//		if(executor != null){
//			try {
//                ThreadPoolFactory.shutdownExecutor(executor);
//			}
//			catch (Exception e){
//				logger.warn("",e);
//			}
//		}
//
//	}
//	private synchronized void buildConsumerAndSubscribe()   {
//        if(shutdown)
//            return;
////		Map<String,Object> properties = consumer.getConsumerPropes();
//
//		if(workThreads != null){
//			executor = ThreadPoolFactory.buildThreadPool(workThreadname,discardRejectMessage == null?"Rocketmq consumer message handle":discardRejectMessage,
//					workThreads,workQueue,blockedWaitTimeout,warnMultsRejects,true,false);
//		}
//
//        this.autoCommit = consumer.isAutoCommit();
//        final ClientServiceProvider provider = ClientServiceProvider.loadService();
//
//        
//        ClientConfigurationBuilder clientConfigurationBuilder = ClientConfiguration.newBuilder();
//        clientConfigurationBuilder.setEndpoints(endpoints);
//                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
//                // client configuration to solve the problem please if SSL is not essential.
//        if(enableSsl != null){
//            clientConfigurationBuilder.enableSsl(enableSsl);
//        }
//        if(accessKey != null && !accessKey.equals("")) {
//            SessionCredentialsProvider sessionCredentialsProvider =
//                    new StaticSessionCredentialsProvider(accessKey, secretKey);
//            clientConfigurationBuilder.setCredentialProvider(sessionCredentialsProvider);
//        }
//        ClientConfiguration clientConfiguration = clientConfigurationBuilder.build();
//        Map<String,FilterExpression> subscriptionExpressions = new LinkedHashMap<>();
//        FilterExpression filterExpression = null;
//        if(tag != null) {
//            filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
//            
//        }
//        else{
//            filterExpression = new FilterExpression("*", FilterExpressionType.TAG);
//        }
//        for(String topic:topics) {
//            subscriptionExpressions.put(topic,filterExpression);
//        }
//        // In most case, you don't need to create too many consumers, singleton pattern is recommended.
//        
//        try {
//            SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
//                    .setClientConfiguration(clientConfiguration)
//                    // Set the consumer group name.
//                    .setConsumerGroup(consumerGroup)
//                    // set await duration for long-polling.
//                    .setAwaitDuration(Duration.ofMillis(awaitDuration))
//                    // Set the subscription for the consumer.
//                    .setSubscriptionExpressions(subscriptionExpressions)                    
//                    .build();
//            this.simpleConsumer = consumer;
//        } catch (ClientException e) {
//            throw new RockemqException(e);
//        }
//       
//	}
//
//	@Override
//	public void run() {
//		try {
//			buildConsumerAndSubscribe();
//            Duration duration = Duration.ofMillis(pollTimeout);
//			while (true) {
//				if (shutdown) {
//					closeConsumer();
//					break;
//				}
//				try {
//                    final List<MessageView> messages = simpleConsumer.receive(this.getMaxPollRecords(), duration);
//                    Future future = null;
//					if(messages != null && !messages.isEmpty()){
//                        future = handleDatas( executor,  simpleConsumer,messages);                       
//					}
//                    if (shutdown) {
//                        future.get();
//                        closeConsumer();
//                        break;
//                    }
//				}
//                catch (RockemqException wakeupException){
//                    closeConsumer();
//                    throw wakeupException;
//                }
//                catch (Exception wakeupException){
//                    closeConsumer();
//                    throw new RockemqException(wakeupException);
//                }
//
//
//
//			}
//		}
//        catch (RockemqException e){
//           throw e;
//        }
//		catch (Throwable e){
//            throw new RockemqException(e);
//		}
//
//	}
//	private void doHandle(SimpleConsumer simpleConsumer,List<MessageView>  records){
//		try {
//			storeService.store(records); 
//            for(MessageView messageView:records){
//                simpleConsumer.ack(messageView);
//            }
//		}
//		catch (ShutdownException e){
//			throw e;
//		}
//        catch (RockemqException e) {
//            throw e;
//        }
//		catch (Exception e) {
//			throw new RockemqException(e);
//		} catch (Throwable e) {
//            throw new RockemqException(e);
//		}
//	}
//	protected Future handleDatas(ExecutorService executor, SimpleConsumer simpleConsumer, List<MessageView> records){
//		if(executor != null) {
//			return executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					doHandle( simpleConsumer,records);
//				}
//			});
//
//		}
//		else{
//			doHandle(simpleConsumer, records);
//            return null;
//		}
//
//	}
//
//    public String getConsumerGroup() {
//        return consumerGroup;
//    }
//
//    public void setConsumerGroup(String consumerGroup) {
//        this.consumerGroup = consumerGroup;
//    }
//
//    public String getAccessKey() {
//        return accessKey;
//    }
//
//    public void setAccessKey(String accessKey) {
//        this.accessKey = accessKey;
//    }
//
//    public String getSecretKey() {
//        return secretKey;
//    }
//
//    public void setSecretKey(String secretKey) {
//        this.secretKey = secretKey;
//    }
//
//    public String getEndpoints() {
//        return endpoints;
//    }
//
//    public void setEndpoints(String endpoints) {
//        this.endpoints = endpoints;
//    }
//
//    public Boolean getEnableSsl() {
//        return enableSsl;
//    }
//
//    public void setEnableSsl(Boolean enableSsl) {
//        this.enableSsl = enableSsl;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public Long getAwaitDuration() {
//        return awaitDuration;
//    }
//
//    public void setAwaitDuration(Long awaitDuration) {
//        this.awaitDuration = awaitDuration;
//    }
////	protected abstract void handleData(BaseKafkaConsumer consumer,ConsumerRecord<Object, Object> record)  throws Exception;
//
// 
//}
