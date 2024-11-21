package org.frameworkset.rocketmq;


import org.frameworkset.rocketmq.codec.RocketmqMessage;

import java.util.List;

/**
 * 数据持久化处理
 */
public interface StoreService<T> {
	public void store(List<RocketmqMessage<T>> messages)  throws Exception ;
}
