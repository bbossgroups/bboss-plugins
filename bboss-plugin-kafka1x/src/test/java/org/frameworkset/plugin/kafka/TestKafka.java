package org.frameworkset.plugin.kafka;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestKafka {
	public static void main(String[] args){
		TestKafka testKafka = new TestKafka();
		testKafka.testSendJsonData(true);
	}
	public void testSendJsonData(boolean syn) {
		KafkaUtil kafkaUtil = new KafkaUtil("kafka.xml");
		KafkaProductor productor = kafkaUtil.getProductor("kafkaproductor");
		List<Map> datas = new ArrayList<>();
		for (int i = 0; i < 10; i++){
			Map<String,Object> data = new HashMap<>();
			data.put("name","duoduo_"+i);
			data.put("classLevel",5);
			data.put("school","师大附小");
			data.put("class","1506");
			data.put("birthDay",new Date());
			data.put("_id",SimpleStringUtil.getUUID());
			datas.add(data);

		}

		Future<RecordMetadata> recordMetadataFuture = productor.send("blackcatstore", (long)12, SimpleStringUtil.object2json(datas));
		if(syn) {
			try {
				RecordMetadata recordMetadata = recordMetadataFuture.get();
				System.out.println(recordMetadata.topic() + "," + recordMetadata.offset() + "," + recordMetadata.partition());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		datas = new ArrayList<>();
		for (int i = 0; i < 10; i++){
			Map<String,Object> data = new HashMap<>();
			data.put("name","duoduo_"+(i+10));
			data.put("classLevel",5);
			data.put("school","师大附小");
			data.put("class","1506");
			data.put("birthDay",new Date());
			data.put("_id",SimpleStringUtil.getUUID());
			datas.add(data);

		}

		recordMetadataFuture = productor.send("blackcatstore", (long)13, SimpleStringUtil.object2json(datas));
		if(syn) {
			try {
				RecordMetadata recordMetadata = recordMetadataFuture.get();
				System.out.println(recordMetadata.topic() + "," + recordMetadata.offset() + "," + recordMetadata.partition());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		Map<String,Object> data = new HashMap<>();
		data.put("name","singleduoduo_"+(10000));
		data.put("classLevel",5);
		data.put("school","师大附小");
		data.put("class","1506");
		data.put("birthDay",new Date());
		data.put("_id",SimpleStringUtil.getUUID());
		recordMetadataFuture = productor.send("blackcatstore", (long)14, SimpleStringUtil.object2json(data));
		if(syn) {
			try {
				RecordMetadata recordMetadata = recordMetadataFuture.get();
				System.out.println(recordMetadata.topic() + "," + recordMetadata.offset() + "," + recordMetadata.partition());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		data = new HashMap<>();
		data.put("name","singleduoduo_"+(10001));
		data.put("classLevel",5);
		data.put("school","师大附小-没有key");
		data.put("class","1506");
		data.put("birthDay",new Date());
		data.put("_id",SimpleStringUtil.getUUID());
		recordMetadataFuture = productor.send("blackcatstore",SimpleStringUtil.object2json(data));
		if(syn) {
			try {
				RecordMetadata recordMetadata = recordMetadataFuture.get();
				System.out.println(recordMetadata.topic() + "," + recordMetadata.offset() + "," + recordMetadata.partition());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	public void testSend(boolean syn) {
		KafkaUtil kafkaUtil = new KafkaUtil("kafka.xml");
		KafkaProductor productor = kafkaUtil.getProductor("kafkaproductor");
		for (int i = 0; i < 100000; i++){
			Future<RecordMetadata> recordMetadataFuture = productor.send("blackcat", (long)i, "aaa" + i);
			if(syn) {
				try {
					RecordMetadata recordMetadata = recordMetadataFuture.get();
					System.out.println(recordMetadata.topic() + "," + recordMetadata.offset() + "," + recordMetadata.partition());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			productor.send("blackcat", (long)i, "bbb" + i);
			productor.send("blackcatbatchstore", (long)i, "aaa" + i);
			productor.send("blackcatbatchstore", (long)i, "bbb" + i);
			productor.send("blackcatstore", (long)i, "aaa" + i);
			productor.send("blackcatstore", (long)i, "bbb" + i);
		}


//		//异步方式发送消息
//		productor.send("blackcat",3l,"aaa",true);
//		productor.send("blackcat",4l,"bbb",true);
//
//		//异步方式发送消息
//		productor.send("blackcat",5l,"aaa",false);
//		productor.send("blackcat",6l,"bbb",false);
	}
}
