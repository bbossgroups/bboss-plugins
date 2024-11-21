package org.frameworkset.rocketmq.codec;
/**
 * Copyright 2024 bboss
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

import org.frameworkset.rocketmq.RockemqException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/21
 */
public abstract class RocketmqCodecUtil {
    public static CodecDeserial convertCodecDeserial(String valueDeserializer, boolean isKey,
                                                     Map<String,Object> consumerPropes){
        CodecDeserial codecDeserial = null;
        if(valueDeserializer != null){
            try {
                Class clazz = Class.forName(valueDeserializer);
                codecDeserial = (CodecDeserial)clazz.getDeclaredConstructor().newInstance();
                codecDeserial.configure(consumerPropes,isKey);
            } catch (InstantiationException e) {
                throw new RockemqException(e);
            } catch (IllegalAccessException e) {
                throw new RockemqException(e);
            } catch (InvocationTargetException e) {
                throw new RockemqException(e);
            } catch (NoSuchMethodException e) {
                throw new RockemqException(e);
            } catch (ClassNotFoundException e) {
                throw new RockemqException(e);
            }
        }
        return codecDeserial;
    }

    public static CodecSerial convertCodecSerial(String valueSerializer, boolean isKey,
                                                     Map<String,Object> productPropes){
        CodecSerial codecSeserial_ = null;
        if(valueSerializer != null){
            try {
                Class clazz = Class.forName(valueSerializer);
                codecSeserial_ = (CodecSerial)clazz.getDeclaredConstructor().newInstance();
                codecSeserial_.configure(productPropes,isKey);
            } catch (InstantiationException e) {
                throw new RockemqException(e);
            } catch (IllegalAccessException e) {
                throw new RockemqException(e);
            } catch (InvocationTargetException e) {
                throw new RockemqException(e);
            } catch (NoSuchMethodException e) {
                throw new RockemqException(e);
            } catch (ClassNotFoundException e) {
                throw new RockemqException(e);
            }
        }
        return codecSeserial_;
    }
}
