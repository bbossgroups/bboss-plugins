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

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.assemble.PropertiesContainer;
import org.frameworkset.spi.assemble.plugin.PropertiesFilePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @date 2020/7/31 11:34
 * @author biaoping.yin
 * @version 1.0
 */
public class ApolloPropertiesFilePlugin implements PropertiesFilePlugin {
	private static Logger logger = LoggerFactory.getLogger(ApolloPropertiesFilePlugin.class);

	//Properties("properties"), XML("xml"), JSON("json"), YML("yml"), YAML("yaml");
	private String[] namespaces;
	private ConfigChangeListener configChangeListener;
	private boolean changeReload;
	@Override
	public String getFiles(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		return extendsAttributes.get("apolloNamespace");
	}

	private String[] parserNamespaces(String namespace){
		String[] ns = namespace.split(",");
		return ns;
	}
	private void configProperties(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes,String ns,
								  ConfigFileFormat configFileFormat,Map datas ){

		Config config = null;
		if(configFileFormat == null)
			config = ConfigService.getConfig(ns);
		else // @to fixed.
		{
			ConfigFile configFile = ConfigService.getConfigFile(ns, configFileFormat);
			String content = configFile.getContent();
		}
//		if(!ResetTag.isFromReset()) {//如果是重置热加载，
//			if (configChangeListener != null) {
//				config.addChangeListener(configChangeListener);
//			} else if (changeReload) {
//				ApplicationContenxtPropertiesListener applicationContenxtPropertiesListener = new ApplicationContenxtPropertiesListener();
//				applicationContenxtPropertiesListener.setGetProperties(applicationContext);
//				config.addChangeListener(applicationContenxtPropertiesListener);
//			}
//		}
		Set<String> pros = config.getPropertyNames();
		if(pros == null || pros.size() == 0)
			return ;
		for(String pname :pros){
			datas.put(pname,config.getProperty(pname,null));
		}
	}
	@Override
	public Map getConfigProperties(BaseApplicationContext applicationContext, Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		String namespace = extendsAttributes.get("apolloNamespace");
		if(namespace == null){
			throw new IllegalArgumentException("must set apolloNamespace for config element. ");

		}
		String _changeReload = extendsAttributes.get("changeReload");
		boolean changeReload = _changeReload != null && _changeReload.equals("true");
		this.changeReload = changeReload;
		String _configChangeListener  = extendsAttributes.get("configChangeListener");

		String _configFileFormat = extendsAttributes.get("configFileFormat");
		ConfigFileFormat configFileFormat = null;
		if(_configFileFormat != null)
		{
			configFileFormat = ConfigFileFormat.fromString(_configFileFormat);
		}
		ConfigChangeListener configChangeListener = null;
		if(_configChangeListener != null && !_configChangeListener.equals("")){
			try {
				configChangeListener = (ConfigChangeListener) Class.forName(_configChangeListener).newInstance();
				if(configChangeListener instanceof PropertiesChangeListener){
					((PropertiesChangeListener)configChangeListener).setPropertiesContainer(propertiesContainer);
				}
				this.configChangeListener = configChangeListener;
			}
			catch (Exception e){
				logger.error("Init configChangeListener:"+configChangeListener +" failed:",e);
			}
		}
		Map datas = new LinkedHashMap();

		String[] ns = parserNamespaces(namespace);
		for(String n:ns){
			configProperties( applicationContext, extendsAttributes,namespace,
					configFileFormat, datas);
		}
		namespaces = ns;
		return datas;
	}

	@Override
	public int getInitType(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		return PropertiesFilePlugin.INIT_TYPE_OUTMAP;
	}

	@Override
	public void restore(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {

	}

	@Override
	/**
	 * 第一次初始化完毕后注册监听器
	 */
	public void afterLoaded(GetProperties applicationContext, PropertiesContainer propertiesContainer) {
		if(!ResetTag.isFromReset()) {//如果是重置热加载，不需重新添加监听器
			if(namespaces != null ) {
				if(configChangeListener instanceof PropertiesChangeListener){
					((PropertiesChangeListener)configChangeListener).completeLoaded();
				}
				for (String ns : namespaces) {
					Config config = ConfigService.getConfig(ns);
					if(config == null)
						continue;
					if (configChangeListener != null) {
						config.addChangeListener(configChangeListener);
					} else if (changeReload) {
						ApplicationContenxtPropertiesListener applicationContenxtPropertiesListener = new ApplicationContenxtPropertiesListener();
						applicationContenxtPropertiesListener.setGetProperties(applicationContext);
						config.addChangeListener(applicationContenxtPropertiesListener);
					}
				}
			}
		}
	}


}
