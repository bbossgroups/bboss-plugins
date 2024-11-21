package org.frameworkset.rocketmq;
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/20
 */
public class GenericClass<T> {
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    public GenericClass() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                clazz = (Class<T>) actualTypeArguments[0];
            } else {
                throw new IllegalArgumentException("无法获取泛型类型");
            }
        } else {
            throw new IllegalArgumentException("父类不是参数化类型");
        }
    }

    public Class<T> getGenericType() {
        return clazz;
    }

    public static class SubClass extends GenericClass<String> {}

    public static void main(String[] args) {
        SubClass subClass = new SubClass();
        System.out.println(subClass.getGenericType()); // 输出: class java.lang.String
    }
}
