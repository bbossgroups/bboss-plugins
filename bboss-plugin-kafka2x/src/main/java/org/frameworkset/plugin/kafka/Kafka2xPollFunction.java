package org.frameworkset.plugin.kafka;
/**
 * Copyright 2025 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

/**
 * @author biaoping.yin
 * @Date 2025/7/28
 */
public class Kafka2xPollFunction implements PollFunction{
    private KafkaConsumer kafkaConsumer;
    private Duration duration ;
    public Kafka2xPollFunction(KafkaConsumer kafkaConsumer, long pollTimeout){
        this.kafkaConsumer = kafkaConsumer;
        this. duration = Duration.ofMillis(pollTimeout);
    }

    @Override
    public ConsumerRecords<Object, Object> poll() {
        return kafkaConsumer.poll(duration);
    }
}
