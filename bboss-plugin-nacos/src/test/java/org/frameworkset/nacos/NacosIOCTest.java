package org.frameworkset.nacos;
/**
 * Copyright 2020 bboss
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

import org.frameworkset.nosql.redis.RedisTool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/8/1 10:38
 * @author biaoping.yin
 * @version 1.0
 */
public class NacosIOCTest {
	private Logger logger = LoggerFactory.getLogger(NacosIOCTest.class);
	@Test
	public void testRedis(){
		RedisTool.getInstance().set("apolloTest","good");
		while(true){
			try {
//				synchronized (this) {
					Thread.currentThread().sleep(1000l);
//				}
				logger.info("apolloTest:"+RedisTool.getInstance().get("apolloTest"));
			}
			catch (Exception e){
                logger.error("",e);
			}
		}
	}
}
