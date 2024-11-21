package org.frameworkset.rocketmq;
/**
 * Copyright 2008 biaoping.yin
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


import org.frameworkset.rocketmq.codec.RocketmqMessage;

import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/9/28 10:41
 * @author biaoping.yin
 * @version 1.0
 */
public class TestRocketmqConsumer2ndStore extends RocketmqConsumer2ndStore<String>{

    @Override
    public void store(List<RocketmqMessage<String>> messages) throws Exception   {
        for(RocketmqMessage<String> message:messages) {
            String data = message.getData();
            String key = message.getMessageExt().getKeys();
            String topic = message.getMessageExt().getTopic();
            int qid = message.getMessageExt().getQueueId();
            long offset = message.getMessageExt().getQueueOffset();
            System.out.println("key=" + key + ",data=" + data + ",topic=" + topic + ",partition=" + qid + ",offset=" + offset);
        }
    }
 


 
}
