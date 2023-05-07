package org.frameworkset.plugin.kafka;

import org.frameworkset.spi.support.ApplicationObjectSupport;
import org.frameworkset.util.shutdown.ShutdownUtil;

import java.util.*;

//import kafka.consumer.ConsumerConfig;
//
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;

public class BaseKafkaConsumer extends ApplicationObjectSupport implements KafkaListener {
//	private BaseApplicationContext applicationContext;
	protected List<BaseKafkaConsumerThread> baseKafkaConsumerThreadList = new ArrayList<>();
	protected String topic;

	public Properties getConsumerPropes() {
		return consumerPropes;
	}

	//	private String zookeeperConnect;
	protected Properties consumerPropes;
	private boolean autoCommit;
//	private KafkaConsumer consumer;
	protected long pollTimeout = 1000l;
	protected int threads = 4;
    private int oldThreads ;
	protected Boolean batch = true;
//	protected ExecutorService executor;
	protected String keyDeserializer;
	protected String valueDeserializer;
	protected Integer maxPollRecords;
	protected Integer workThreads ;
    private Object lock = new Object();

	public long getBlockedWaitTimeout() {
		return blockedWaitTimeout;
	}

	public void setBlockedWaitTimeout(long blockedWaitTimeout) {
		this.blockedWaitTimeout = blockedWaitTimeout;
	}

	protected long blockedWaitTimeout = -1;

	public int getWarnMultsRejects() {
		return warnMultsRejects;
	}

	public void setWarnMultsRejects(int warnMultsRejects) {
		this.warnMultsRejects = warnMultsRejects;
	}

	protected int warnMultsRejects = 500;
	protected Integer workQueue = 100;
	private String groupId;
	public void setThreads(int threads) {
		this.threads = threads;
	}
	protected String discardRejectMessage;
	public String getDiscardRejectMessage() {
		return discardRejectMessage;
	}

	public void setDiscardRejectMessage(String discardRejectMessage) {
		this.discardRejectMessage = discardRejectMessage;
	}

	public int getThreads() {
		return threads;
	}

	public String getTopic() {
		return topic;
	}

	public void setBatch(Boolean batch) {
		this.batch = batch;
	}

	public Boolean getBatch() {
		return batch;
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



	public long getPollTimeout() {
		return pollTimeout;
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

	public void setPollTimeout(long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}



	public void setConsumerPropes(Properties consumerPropes) {
		this.consumerPropes = consumerPropes;
	}

	protected  volatile boolean shutdown ;
    private Object shutdownlock = new Object();
	public void shutdown(){
		synchronized (shutdownlock) {
			if(shutdown)
				return;
			shutdown = true;
		}

        synchronized (lock) {
            if (baseKafkaConsumerThreadList.size() > 0) {
                for (BaseKafkaConsumerThread baseKafkaConsumerThread : baseKafkaConsumerThreadList) {
                    baseKafkaConsumerThread.shutdown();
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
	
	public BaseKafkaConsumer() {
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
		String _autoCommit = this.consumerPropes.getProperty("enable.auto.commit","true");
		if(_autoCommit.equals("true")){
			this.autoCommit = true;
		}
		else{
			this.autoCommit = false;
		}
//		 consumerConfig = new ConsumerConfig(productorPropes);
	}
	protected StoreService storeService = null;

    public void run(boolean addShutdownHook){
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        final String[] topics = topic.split("\\,");
        int a_numThreads = threads;
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
            for (int i = 0; i < a_numThreads; i++) {
                BaseKafkaConsumerThread runnable = buildRunnable(i, topics);
                baseKafkaConsumerThreadList.add(runnable);
                runnable.start();

            }
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

    /**
     * 增加给定数量的消费线程
     * @param increamentConsumerTheads
     */
    public void increamentConsumerThead(int increamentConsumerTheads){
        if(increamentConsumerTheads <= 0){
            throw new IllegalArgumentException("Increament Consumer Theads " + increamentConsumerTheads + " must > 0. ");
        }
        final String[] topics = topic.split("\\,");
        synchronized (lock) {
            this.oldThreads = threads;
            threads = increamentConsumerTheads + threads;
            logger.info("Consumer theads:{} before increament ", oldThreads);
            int a_numThreads = increamentConsumerTheads;
            for (int i = 0; i < a_numThreads; i++) {
                BaseKafkaConsumerThread runnable = buildRunnable(oldThreads + i, topics);
                baseKafkaConsumerThreadList.add(runnable);
                runnable.start();

            }
        }
        logger.info("Consumer theads:{} after increament ",threads);

    }

    /**
     * 重置消费线程数量
     * @param newThreads
     */
    public void resetConsumerThreads(int newThreads){
        if(newThreads <= 0){
            throw new IllegalArgumentException("Consumer Theads " + newThreads + " must > 0. ");
        }
        synchronized (lock) {
            if(newThreads > threads){
                this.increamentConsumerThead(newThreads - threads);
            }
            else if(newThreads < threads){
                this.decreamentConsumerThead(threads - newThreads);
            }

        }
    }
    /**
     * 消减给定数量的消费线程
     * @param decreamentConsumerTheads
     */
    public void decreamentConsumerThead(int decreamentConsumerTheads){
        if(decreamentConsumerTheads <= 0){
            throw new IllegalArgumentException("Decreament Consumer Theads " + decreamentConsumerTheads + " must > 0. ");
        }
        final String[] topics = topic.split("\\,");
        synchronized (lock) {
            this.oldThreads = threads;

            threads = threads - decreamentConsumerTheads;
            if (threads < 0)
                threads = 0;
            logger.info("Consumer theads:{} before decreament ", oldThreads);
            for (int i = 0; i < decreamentConsumerTheads; i++) {
                int pos = oldThreads - i - 1;
                if (pos >= 0) {
                    BaseKafkaConsumerThread baseKafkaConsumerThread = baseKafkaConsumerThreadList.remove(pos);
                    baseKafkaConsumerThread.shutdown();
                } else {

                    break;
                }
            }
        }
        logger.info("Consumer theads:{} after decreament ",threads);

    }

	@Override
	public void run() {

        run(true);

	}
	
	protected BaseKafkaConsumerThread buildRunnable(int partition,String[] topic){
		BaseKafkaConsumerThread baseKafkaConsumerThread = new BaseKafkaConsumerThread(partition,this,topic,storeService);
		baseKafkaConsumerThread.setKeyDeserializer(keyDeserializer);
		baseKafkaConsumerThread.setValueDeserializer(valueDeserializer);
		baseKafkaConsumerThread.setMaxPollRecords(maxPollRecords);
		baseKafkaConsumerThread.setPollTimeout(pollTimeout);
		baseKafkaConsumerThread.setWorkThreads(workThreads);
		baseKafkaConsumerThread.setWorkQueue(workQueue);
		baseKafkaConsumerThread.setBatch(batch);
		baseKafkaConsumerThread.setBlockedWaitTimeout(blockedWaitTimeout);
		baseKafkaConsumerThread.setDiscardRejectMessage(discardRejectMessage);
		baseKafkaConsumerThread.setWarnMultsRejects(warnMultsRejects);
		baseKafkaConsumerThread.setGroupId(groupId);
		return baseKafkaConsumerThread;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

//	/**
//	 *
//	 */
//	public void commitOffset(){
//		this.consumer.commitSync();
//	}
//
//	public void commitOffsets(boolean retryOnFailure){
//		this.consumer.commitSync();
////		this.consumer.commitOffsets(retryOnFailure);
//	}
//
//	/**
//	 *  Commit offsets using the provided offsets map
//	 *
//	 *  @param offsetsToCommit a map containing the offset to commit for each partition.
//	 *  @param retryOnFailure enable retries on the offset commit if it fails.
//	 */
//	public void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsetsToCommit, boolean retryOnFailure){
//		this.consumer.commitSync(offsetsToCommit);
//	}

}
