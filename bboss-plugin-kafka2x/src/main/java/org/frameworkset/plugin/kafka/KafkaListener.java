package org.frameworkset.plugin.kafka;

public interface KafkaListener extends Runnable{
    public void shutdown();
    public void run(boolean addShutdownHook);
}
