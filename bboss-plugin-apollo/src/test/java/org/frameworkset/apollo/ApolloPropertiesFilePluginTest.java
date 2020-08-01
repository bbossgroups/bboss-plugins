package org.frameworkset.apollo;
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

import org.frameworkset.spi.assemble.PropertiesContainer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/7/31 11:34
 * @author biaoping.yin
 * @version 1.0
 */
public class ApolloPropertiesFilePluginTest{
	private static Logger logger = LoggerFactory.getLogger(ApolloPropertiesFilePlugin.class);
	PropertiesContainer propertiesContainer ;
	@Before
	public void init(){
		propertiesContainer = new PropertiesContainer();
		propertiesContainer.addConfigPropertiesFromApollo("application",true);
	}
	@Test
	public void test(){
		dbinfo("");
		dbinfo("ecop.");
		while(true){
			try {
				synchronized (this) {
					Thread.currentThread().wait(1000l);
				}
			}
			catch (Exception e){

			}
		}

	}

	private void dbinfo(String dbname){
		String dbName  = propertiesContainer.getProperty(dbname+"db.name");
		String dbUser  = propertiesContainer.getProperty(dbname+"db.user");
		String dbPassword  = propertiesContainer.getProperty(dbname+"db.password");
		String dbDriver  = propertiesContainer.getProperty(dbname+"db.driver");
		String dbUrl  = propertiesContainer.getProperty(dbname+"db.url");

		String showsql  = propertiesContainer.getProperty(dbname+"db.showsql");
		String validateSQL  = propertiesContainer.getProperty(dbname+"db.validateSQL");
		String dbInfoEncryptClass = propertiesContainer.getProperty(dbname+"db.dbInfoEncryptClass");
		System.out.println("dbName:"+dbName);
		System.out.println("dbUser:"+dbUser);
		System.out.println("dbPassword:"+dbPassword);
		System.out.println("dbDriver:"+dbDriver);
		System.out.println("dbUrl:"+dbUrl);
		System.out.println("showsql:"+showsql);
		System.out.println("validateSQL:"+validateSQL);
		System.out.println("dbInfoEncryptClass:"+dbInfoEncryptClass);
	}
}
