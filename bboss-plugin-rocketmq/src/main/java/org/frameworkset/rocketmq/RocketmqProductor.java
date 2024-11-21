package org.frameworkset.rocketmq;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.frameworkset.rocketmq.codec.CodecSerial;
import org.frameworkset.rocketmq.codec.StringBytesCodecSerial;
import org.frameworkset.rocketmq.codec.StringCodecSerial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;



public class RocketmqProductor {
	private DefaultMQProducer producer = null;
	private static final Logger logger = LoggerFactory.getLogger(RocketmqProductor.class);
	private boolean sendDatatoRocketmq = true;
    private Map<String,Object> productorPropes;
    
    private String productGroup;
    private String namesrvAddr;
    private String valueCodecSerial;
    private String keyCodecSerial;

    private String accessKey;
    private String secretKey;
    private String securityToken;
    private String signature;


    private Boolean enableSsl ;

    private CodecSerial<byte[]> valueCodecSerial_;
    private CodecSerial<String> keyCodecSerial_;
    private boolean destroied;

    public String getValueCodecSerial() {
        return valueCodecSerial;
    }

    public void setValueCodecSerial(String valueCodecSerial) {
        this.valueCodecSerial = valueCodecSerial;
    }

    public String getKeyCodecSerial() {
        return keyCodecSerial;
    }

    public void setKeyCodecSerial(String keyCodecSerial) {
        this.keyCodecSerial = keyCodecSerial;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void destroy(){
		synchronized (this) {
			if(destroied)
				return;
			destroied = true;
		}

		try {
			if (producer != null) {
				producer.shutdown();
			}
		}
		catch (Exception e){
			logger.warn("",e);
		}
	}
	public RocketmqProductor() {
		 
		
	}
	
	public void init(){
		if(sendDatatoRocketmq) {
            try {
                /*
                 * Instantiate with a producer group name.
                 */
                AclClientRPCHook auth = null;
                if(SimpleStringUtil.isNotEmpty(accessKey)) {
                    SessionCredentials sessionCredentials = new SessionCredentials(accessKey, secretKey, securityToken);
                    sessionCredentials.setSignature(signature);
                    auth = new AclClientRPCHook(sessionCredentials);
                }
                DefaultMQProducer producer = new DefaultMQProducer(productGroup,auth);
                if(SimpleStringUtil.isNotEmpty(valueCodecSerial) ){
                    Class c = Class.forName(valueCodecSerial);
                    valueCodecSerial_ = (CodecSerial) c.getDeclaredConstructor().newInstance();
                }
                else{
                    valueCodecSerial_ = new StringBytesCodecSerial();
                }
    
                if(SimpleStringUtil.isNotEmpty(keyCodecSerial) ){
                    Class c = Class.forName(keyCodecSerial);
                    keyCodecSerial_ = (CodecSerial) c.getDeclaredConstructor().newInstance();
                }
                else{
                    keyCodecSerial_ = new StringCodecSerial();
                }
                /*
                 * Specify name server addresses.
                 *
                 * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
                 * <pre>
                 * {@code
                 *  producer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
                 * }
                 * </pre>
                 */
                // Uncomment the following line while debugging, namesrvAddr should be set to your local address
                producer.setNamesrvAddr(namesrvAddr);
    
                if(enableSsl != null){
                    producer.setUseTLS(enableSsl);
                }
                
                /*
                 * Launch the instance.
                 */
            
                producer.start();
                this.producer = producer;
            } catch (Exception e) {
                throw new RockemqException(e);
            }
        }


	}
    public void send(final String topic,  final Object data ){
        send(  topic,  null, data, (String)null, (SendCallback)null);
    }
    public void send(final String topic,  final Object data,   SendCallback callback){
        send(  topic,  null, data, (String)null, callback);
    }

    public void sendWithTag(final String topic,  final Object data,String tag,   SendCallback callback){
        send(  topic,  null, data, tag, callback);
    }

    public void sendWithKeys(final String topic, final Object keys, final Object data,  SendCallback callback){
        send(  topic,  keys, data, (String)null, callback);
    }
    public void sendWithTag(final String topic,  final Object data,String tag ){
        send(  topic,  null, data, tag, (SendCallback)null);
    }

    public void sendWithKeys(final String topic, final Object keys, final Object data ){
        send(  topic,  keys, data, (String)null, (SendCallback)null);
    }


    public void sendOneway(final String topic, final Object keys, final Object data, String tag){
        send(  topic,  keys, data, tag, (SendCallback)null);
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void send(final String topic, final Object keys, final Object data, String tag, SendCallback callback){

		if(sendDatatoRocketmq){
			if(producer != null) {
                /*
                 * Create a message instance, specifying topic, tag and message body.
                 */
                Message msg = new Message(topic /* Topic */,
                        tag /* Tag */,
                        keys != null?keyCodecSerial_.serial(keys):null,/* keys*/
                        valueCodecSerial_.serial(data) /* Message body */
                );

              
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
                try {
                    if(callback != null) {
                        producer.send(msg, callback);
                    }
                    else{
                        producer.sendOneway(msg);
                    }
                } catch (MQClientException e) {
                    throw new RockemqException(e);
                } catch (RemotingException e) {
                    throw new RockemqException(e);
                } catch (InterruptedException e) {
                    throw new RockemqException(e);
                }
            }
            else {
                if (logger.isInfoEnabled())
                    logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} and producer is null", sendDatatoRocketmq);
            }
		}
		else{
			if(logger.isInfoEnabled())
				logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} ", sendDatatoRocketmq);
		}
	}
    public void send(Message msg){
        send(msg,(SendCallback)null);
    }
    public void send(Message msg, SendCallback callback){

        if(sendDatatoRocketmq){
            if(producer != null) {
                


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
                try {
                    if(callback != null) {
                        producer.send(msg, callback);
                    }
                    else{
                        producer.sendOneway(msg);
                    }
                } catch (MQClientException e) {
                    throw new RockemqException(e);
                } catch (RemotingException e) {
                    throw new RockemqException(e);
                } catch (InterruptedException e) {
                    throw new RockemqException(e);
                }
            }
            else {
                if (logger.isInfoEnabled())
                    logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} and producer is null", sendDatatoRocketmq);
            }
        }
        else{
            if(logger.isInfoEnabled())
                logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} ", sendDatatoRocketmq);
        }
    }
    public SendResult sendSynWithKeys(final String topic, final Object keys, final Object data){
        return send(  topic,  keys,   data, (String)null);
    }
    public SendResult sendSynWithTag(final String topic,   final Object data, String tag){
        return send(  topic,  null,   data, tag);
    }
    public SendResult send(Message msg,
                           long timeout){
        if(sendDatatoRocketmq){
            if(producer != null) {
                


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
                try {
                    SendResult sendResult = producer.send(msg,timeout);
                    return sendResult;
                } catch (MQClientException e) {
                    throw new RockemqException(e);
                } catch (RemotingException e) {
                    throw new RockemqException(e);
                } catch (InterruptedException e) {
                    throw new RockemqException(e);
                } catch (MQBrokerException e) {
                    throw new RockemqException(e);
                }
            }
            else {
                if (logger.isInfoEnabled())
                    logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} and producer is null", sendDatatoRocketmq);
            }
        }
        else{
            if(logger.isInfoEnabled())
                logger.info("Ignore send Data to Kafka:sendDatatoRocketmq={} ", sendDatatoRocketmq);
        }
        return null;
    }
    public SendResult sendSyn(final String topic, final Object data){
        return send( topic, null, data, (String)null);
    }
    public SendResult send(final String topic, final Object keys, final Object data, String tag){

        if(sendDatatoRocketmq){
            if(producer != null) {
                /*
                 * Create a message instance, specifying topic, tag and message body.
                 */
                Message msg = new Message(topic /* Topic */,
                        tag /* Tag */,
                        keys != null?keyCodecSerial_.serial(keys):null,/* keys*/
                        valueCodecSerial_.serial(data) /* Message body */
                );


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
                try {
                    SendResult sendResult = producer.send(msg);
                    return sendResult;
                } catch (MQClientException e) {
                    throw new RockemqException(e);
                } catch (RemotingException e) {
                    throw new RockemqException(e);
                } catch (InterruptedException e) {
                    throw new RockemqException(e);
                } catch (MQBrokerException e) {
                    throw new RockemqException(e);
                }
            }
            else {
                if (logger.isInfoEnabled())
                    logger.info("Ignore send Data to Rocketmq:sendDatatoRocketmq={} and producer is null", sendDatatoRocketmq);
            }
        }
        else{
            if(logger.isInfoEnabled())
                logger.info("Ignore send Data to Kafka:sendDatatoRocketmq={} ", sendDatatoRocketmq);
        }
        return null;
    }



	public Map<String,Object> getProductorPropes() {
		return productorPropes;
	}
   
	public void setProductorPropes(Map<String,Object> productorPropes) {
		this.productorPropes = productorPropes;
	}

	public boolean isSendDatatoRocketmq() {
		return sendDatatoRocketmq;
	}

	public void setSendDatatoRocketmq(boolean sendDatatoRocketmq) {
		this.sendDatatoRocketmq = sendDatatoRocketmq;
	}


    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(Boolean enableSsl) {
        this.enableSsl = enableSsl;
    }
}
