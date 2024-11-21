package org.frameworkset.rocketmq;

public interface RocketMQListener extends Runnable{
    public void shutdown();
    public void run(boolean addShutdownHook);
}
