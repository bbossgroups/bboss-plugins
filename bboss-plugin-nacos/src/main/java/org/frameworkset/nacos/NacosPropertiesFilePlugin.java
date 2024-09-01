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

 
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.config.ResetTag;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.assemble.AssembleException;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.assemble.PropertiesContainer;
import org.frameworkset.spi.assemble.plugin.PropertiesFilePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @date 2020/7/31 11:34
 * @author biaoping.yin
 * @version 1.0
 */
public class NacosPropertiesFilePlugin implements PropertiesFilePlugin {
	private static Logger logger = LoggerFactory.getLogger(NacosPropertiesFilePlugin.class);

	//Properties("properties"), XML("xml"), JSON("json"), YML("yml"), YAML("yaml");
	private String namespace;
    private String serverAddr;
    private String dataId;
    private String dataIds[];
    private String group;
    private long timeOut;
	private Listener configChangeListener;
    private ConfigService configService = null;
	private boolean changeReload;
    private Map<String,String> extendsAttributes;
	@Override
	public String getFiles(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		return extendsAttributes.get(PropertiesContainer.nacosNamespaceName);
	}

 
    public Properties getProperties(String configInfo) throws IOException {
        StringReader reader = null;
        try {
            Properties properties1 = new Properties();
            if(configInfo == null)
                return properties1;
            reader = new StringReader(configInfo);
            properties1.load(reader);
            return properties1;
        }
        finally {
            if(reader != null){
                reader.close();
            }
        }
    }

    /**
     * public static final String IS_USE_CLOUD_NAMESPACE_PARSING = "isUseCloudNamespaceParsing";
     *
     *     public static final String IS_USE_ENDPOINT_PARSING_RULE = "isUseEndpointParsingRule";
     *
     *     public static final String ENDPOINT = "endpoint";
     *
     *     public static final String ENDPOINT_QUERY_PARAMS = "endpointQueryParams";
     *
     *     public static final String ENDPOINT_PORT = "endpointPort";
     *
     *     public static final String ENDPOINT_CONTEXT_PATH = "endpointContextPath";
     *
     *     public static final String ENDPOINT_CLUSTER_NAME = "endpointClusterName";
     *
     *     public static final String SERVER_NAME = "serverName";
     *
     *     public static final String NAMESPACE = "namespace";
     *
     *     public static final String USERNAME = "username";
     *
     *     public static final String PASSWORD = "password";
     *
     *     public static final String ACCESS_KEY = "accessKey";
     *
     *     public static final String SECRET_KEY = "secretKey";
     *
     *     public static final String RAM_ROLE_NAME = "ramRoleName";
     *
     *     public static final String SERVER_ADDR = "serverAddr";
     *
     *     public static final String CONTEXT_PATH = "contextPath";
     *
     *     public static final String CLUSTER_NAME = "clusterName";
     *
     *     public static final String ENCODE = "encode";
     *
     *     public static final String CONFIG_LONG_POLL_TIMEOUT = "configLongPollTimeout";
     *
     *     public static final String CONFIG_RETRY_TIME = "configRetryTime";
     *
     *     public static final String CLIENT_WORKER_MAX_THREAD_COUNT = "clientWorkerMaxThreadCount";
     *
     *     public static final String CLIENT_WORKER_THREAD_COUNT = "clientWorkerThreadCount";
     *
     *     public static final String MAX_RETRY = "maxRetry";
     *
     *     public static final String ENABLE_REMOTE_SYNC_CONFIG = "enableRemoteSyncConfig";
     *
     *     public static final String NAMING_LOAD_CACHE_AT_START = "namingLoadCacheAtStart";
     *
     *     public static final String NAMING_CACHE_REGISTRY_DIR = "namingCacheRegistryDir";
     *
     *     public static final String NAMING_CLIENT_BEAT_THREAD_COUNT = "namingClientBeatThreadCount";
     *
     *     public static final String NAMING_POLLING_MAX_THREAD_COUNT = "namingPollingMaxThreadCount";
     *
     *     public static final String NAMING_POLLING_THREAD_COUNT = "namingPollingThreadCount";
     *
     *     public static final String NAMING_REQUEST_DOMAIN_RETRY_COUNT = "namingRequestDomainMaxRetryCount";
     *
     *     public static final String NAMING_PUSH_EMPTY_PROTECTION = "namingPushEmptyProtection";
     *
     *     public static final String NAMING_ASYNC_QUERY_SUBSCRIBE_SERVICE = "namingAsyncQuerySubscribeService";
     *
     *     public static final String REDO_DELAY_TIME = "redoDelayTime";
     *
     *     public static final String REDO_DELAY_THREAD_COUNT = "redoDelayThreadCount";
     *
     *     public static final String SIGNATURE_REGION_ID = "signatureRegionId";
     *
     *     public static final String LOG_ALL_PROPERTIES = "logAllProperties";
     * @param ns
     * @param serverAddr
     * @param extendsAttributes
     * @return
     */
    private Properties buildProperties(String ns,String serverAddr,Map<String,String> extendsAttributes){
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, ns);
        Map<String,String> configs = new HashMap<String,String>();
        configs.putAll(extendsAttributes);
        configs.remove(PropertiesContainer.nacosNamespaceName);
        configs.remove(PropertyKeyConst.SERVER_ADDR);
        configs.remove(PropertyKeyConst.NAMESPACE);

        configs.remove("dataId");

        configs.remove("group");

        configs.remove("timeOut");
        configs.remove("changeReload");
        configs.remove("configChangeListener");
        configs.remove("configFileFormat");
        properties.putAll(configs);
        return properties;
    }
	private void configProperties(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes,
                                  String ns,String serverAddr,String dataId,String group,long timeOut,
								  Map datas ){
        StringReader reader = null;
        try {
            if(SimpleStringUtil.isEmpty(ns)){
                throw new AssembleException("Nacos Properties load failed:namespace is null or empty,extendsAttributes is "+SimpleStringUtil.object2json(extendsAttributes));
            }
            if(SimpleStringUtil.isEmpty(serverAddr)){
                throw new AssembleException("Nacos Properties load failed:serverAddr is null or empty,extendsAttributes is "+SimpleStringUtil.object2json(extendsAttributes));
            }
            if(SimpleStringUtil.isEmpty(dataId)){
                throw new AssembleException("Nacos Properties load failed:dataId is null or empty,extendsAttributes is "+SimpleStringUtil.object2json(extendsAttributes));
            }
            if(SimpleStringUtil.isEmpty(group)){
                throw new AssembleException("Nacos Properties load failed:group is null or empty,extendsAttributes is "+SimpleStringUtil.object2json(extendsAttributes));
            }
            String[] dataIds = dataId.split(",");
            this.dataIds = dataIds;
            if(configService == null) {
                Properties properties = buildProperties(  ns,  serverAddr,  extendsAttributes);
//                properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
//                properties.put(PropertyKeyConst.NAMESPACE, ns);
                ConfigService configService = NacosFactory.createConfigService(properties);
              
                this.configService = configService;
            }
            
            for(String dataId_:dataIds) {
                String configInfo = configService.getConfig(dataId_, group, timeOut);

                Properties properties1 = getProperties(configInfo);
                if (properties1.size() == 0)
                    continue;
                datas.putAll(properties1);
            }
            
        }
        catch (Exception e){
            throw new AssembleException("Nacos Properties load failed:"+SimpleStringUtil.object2json(extendsAttributes),e);
        }
        finally {
            if(reader != null) {
                reader.close();
            }
        }
		 
	}
	@Override
	public Map getConfigProperties(BaseApplicationContext applicationContext, Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		String namespace = extendsAttributes.get(PropertiesContainer.nacosNamespaceName);
		if(namespace == null){
			throw new IllegalArgumentException("must set nacosNamespace for config element. ");

		}
        this.extendsAttributes = extendsAttributes;
        serverAddr = extendsAttributes.get("serverAddr");
        dataId = extendsAttributes.get("dataId");
        group = extendsAttributes.get("group");
        String timeOut_ = extendsAttributes.get("timeOut");
        if(SimpleStringUtil.isNotEmpty(timeOut_) )
        {
            timeOut = Long.parseLong(timeOut_);
        }
		String _changeReload = extendsAttributes.get("changeReload");
		boolean changeReload = _changeReload != null && _changeReload.equals("true");
		this.changeReload = changeReload;
		String _configChangeListener  = extendsAttributes.get("configChangeListener");

		String _configFileFormat = extendsAttributes.get("configFileFormat");
		 
		Listener configChangeListener = null;
		if(this.configChangeListener == null && _configChangeListener != null && !_configChangeListener.equals("")){
			try {
				configChangeListener = (Listener) Class.forName(_configChangeListener).newInstance();
				if(configChangeListener instanceof PropertiesChangeListener){
                    PropertiesChangeListener propertiesChangeListener = (PropertiesChangeListener)configChangeListener;
                    propertiesChangeListener.setPropertiesContainer(propertiesContainer);
                    propertiesChangeListener.setApplicationContext(applicationContext);
                    propertiesChangeListener.setNacosPropertiesFilePlugin(this);
				}
				this.configChangeListener = configChangeListener;
			}
			catch (Exception e){
				logger.error("Init configChangeListener:"+configChangeListener +" failed:",e);
			}
		}
		Map datas = new LinkedHashMap();

//		String[] ns = parserNamespaces(namespace);
//		for(String n:ns){
        configProperties( applicationContext, extendsAttributes,namespace,serverAddr,dataId,group,timeOut,
                 datas);
//		}
		this.namespace = namespace;
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
			if(configChangeListener == null && changeReload) {
                ApplicationContenxtPropertiesListener applicationContenxtPropertiesListener = new ApplicationContenxtPropertiesListener();
                applicationContenxtPropertiesListener.setGetProperties(applicationContext);
                applicationContenxtPropertiesListener.setNacosPropertiesFilePlugin(this);
                configChangeListener = applicationContenxtPropertiesListener;
			}           


            if (configChangeListener != null) {
                if(configChangeListener instanceof PropertiesChangeListener){
                    ((PropertiesChangeListener)configChangeListener).completeLoaded();
                }
                try {
                    for (String dataId_ : dataIds) {
                        configService.addListener(dataId_, group, configChangeListener);
                    }
//                        configService.addListener(dataId,group,configChangeListener);
                } catch (NacosException e) {
                    throw new AssembleException(e);
                }
            }
		}
	}

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
