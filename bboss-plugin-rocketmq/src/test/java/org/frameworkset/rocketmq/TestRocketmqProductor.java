package org.frameworkset.rocketmq;

import org.apache.rocketmq.client.producer.SendResult;

public class TestRocketmqProductor {

	public static void main(String[] args) {
        RocketmqUtil rocketmqUtil = new RocketmqUtil("rocketmq-productor.xml");
        RocketmqProductor rocketmqProductor = rocketmqUtil.getProductor("rocketmqproductor");

        for (int i = 0; i < 10000; i++) {
            try {
                SendResult sendResult = rocketmqProductor.sendSynWithTag("TestTopic","Hello RocketMQ " + i,"A");
               
                /*
                 * There are different ways to send message, if you don't care about the send result,you can use this way
                 * {@code
                 * producer.sendOneway(msg);
                 * }
                 */

                /*
                 * if you want to get the send result in a synchronize way, you can use this send method
                 * {@code
                 * SendResult sendResult = producer.send(msg);
                 * System.out.printf("%s%n", sendResult);
                 * }
                 */

                /*
                 * if you want to get the send result in a asynchronize way, you can use this send method
                 * {@code
                 *
                 *  producer.send(msg, new SendCallback() {
                 *  @Override
                 *  public void onSuccess(SendResult sendResult) {
                 *      // do something
                 *  }
                 *
                 *  @Override
                 *  public void onException(Throwable e) {
                 *      // do something
                 *  }
                 *});
                 *
                 *}
                 */

                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        rocketmqProductor.destroy();
	}

}
