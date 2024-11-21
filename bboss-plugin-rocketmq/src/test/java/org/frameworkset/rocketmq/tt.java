package org.frameworkset.rocketmq;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/20
 */
public class tt<T> {
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    public tt() {
        this.clazz = (Class<T>) getSuperClassGenricType(getClass(), 0);
    }

    public Class<T> getGenericType() {
        return clazz;
    }

    @SuppressWarnings("rawtypes")
    private static Class getSuperClassGenricType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

    public static void main(String[] args) {
        tt<String> genericClass = new tt<String>() {};
        System.out.println(genericClass.getGenericType()); // 输出: class java.lang.String
    }
}
