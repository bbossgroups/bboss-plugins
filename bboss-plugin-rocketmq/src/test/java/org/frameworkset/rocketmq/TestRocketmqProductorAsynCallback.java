package org.frameworkset.rocketmq;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

public class TestRocketmqProductorAsynCallback {

	public static void main(String[] args) {
        RocketmqUtil rocketmqUtil = new RocketmqUtil("rocketmq-productor.xml");
        RocketmqProductor rocketmqProductor = rocketmqUtil.getProductor("rocketmqproductor");

        for (int i = 0; i < 10000; i++) {
            try {
                rocketmqProductor.send("TestTopic", "testKey", "Hello RocketMQ " + i, "A", new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%s%n", sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                    }
                });
               
                

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        rocketmqProductor.destroy();
	}

}
