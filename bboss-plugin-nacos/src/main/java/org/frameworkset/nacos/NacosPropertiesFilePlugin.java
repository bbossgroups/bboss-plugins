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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
    private String group;
    private long timeOut;
	private Listener configChangeListener;
    private ConfigService configService = null;
	private boolean changeReload;
	@Override
	public String getFiles(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		return extendsAttributes.get("nacosNamespace");
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
	private void configProperties(BaseApplicationContext applicationContext,Map<String,String> extendsAttributes,
                                  String ns,String serverAddr,String dataId,String group,long timeOut,
								  Map datas ){
        StringReader reader = null;
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.NAMESPACE, ns);
            ConfigService configService = NacosFactory.createConfigService(properties);
            this.configService = configService;
            String configInfo = configService.getConfig(dataId, group, timeOut);
//		if(!ResetTag.isFromReset()) {//如果是重置热加载，
//			if (configChangeListener != null) {
//				config.addChangeListener(configChangeListener);
//			} else if (changeReload) {
//				ApplicationContenxtPropertiesListener applicationContenxtPropertiesListener = new ApplicationContenxtPropertiesListener();
//				applicationContenxtPropertiesListener.setGetProperties(applicationContext);
//				config.addChangeListener(applicationContenxtPropertiesListener);
//			}
//		}
            Properties properties1 = getProperties(configInfo) ;
            if (properties1.size() == 0)
                return;
            datas.putAll(properties1);
            
        }
        catch (Exception e){
            throw new AssembleException("Nacos Properties load failed:serverAddr="+serverAddr
                    +",NAMESPACE="+ns+",dataId="+dataId+",group="+group+",timeOut="+timeOut,e);
        }
        finally {
            if(reader != null) {
                reader.close();
            }
        }
		 
	}
	@Override
	public Map getConfigProperties(BaseApplicationContext applicationContext, Map<String,String> extendsAttributes, PropertiesContainer propertiesContainer) {
		String namespace = extendsAttributes.get("nacosNamespace");
		if(namespace == null){
			throw new IllegalArgumentException("must set nacosNamespace for config element. ");

		}
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
		if(_configChangeListener != null && !_configChangeListener.equals("")){
			try {
				configChangeListener = (Listener) Class.forName(_configChangeListener).newInstance();
				if(configChangeListener instanceof PropertiesChangeListener){
                    PropertiesChangeListener propertiesChangeListener = (PropertiesChangeListener)configChangeListener;
                    propertiesChangeListener.setPropertiesContainer(propertiesContainer);
                    propertiesChangeListener.setApplicationContext(applicationContext);
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
			if(namespace != null ) {
				if(configChangeListener instanceof PropertiesChangeListener){
					((PropertiesChangeListener)configChangeListener).completeLoaded();
				}
				
				 
                if (configChangeListener != null) {
                    try {
                        configService.addListener(dataId,group,configChangeListener);
                    } catch (NacosException e) {
                        throw new AssembleException(e);
                    }
                } else if (changeReload) {
                    ApplicationContenxtPropertiesListener applicationContenxtPropertiesListener = new ApplicationContenxtPropertiesListener();
                    applicationContenxtPropertiesListener.setGetProperties(applicationContext);
                    try {
                        configService.addListener(dataId,group,configChangeListener);
                    } catch (NacosException e) {
                        throw new AssembleException(e);
                    }
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
