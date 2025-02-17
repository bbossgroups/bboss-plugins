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

 
import org.frameworkset.spi.assemble.AssembleException;
import org.frameworkset.spi.remote.http.HttpHost;
import org.frameworkset.spi.remote.http.proxy.HttpProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * <p>Description: http proxy nacos 服务自动发现和路由动态切换监听器 </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @date 2020/8/2 20:07
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpProxyConfigChangeListener extends PropertiesChangeListener {
	private static Logger logger = LoggerFactory.getLogger(HttpProxyConfigChangeListener.class);
	private Set<String> httpPools;
	String defaultHostsKey = "http.hosts";
	String defaultRoutingKey = "http.routing";
    String defaultEsHostsKey = "elasticsearch.rest.hostNames";
	private void handleDiscoverHosts(String _hosts,String poolName,String changeRouting){
		if(_hosts != null && !_hosts.equals("")){
			String[] hosts = _hosts.split(",");
			List<HttpHost> httpHosts = new ArrayList<HttpHost>();
			HttpHost host = null;
			for(int i = 0; i < hosts.length; i ++){
				String hosts_ = hosts[i].trim();
				if(!hosts_.equals("")) {
					host = new HttpHost(hosts_);
					httpHosts.add(host);
				}
			}
			//将被动获取到的地址清单加入服务地址组poolName中
			if(httpHosts.size() > 0){
				HttpProxyUtil.handleDiscoverHosts(poolName,httpHosts,changeRouting);
			}
		}
	}
    /**
     * //模拟被动获取监听地址清单
     * List<HttpHost> hosts = new ArrayList<HttpHost>();
     * // https服务必须带https://协议头,例如https://192.168.137.1:808
     * HttpHost host = new HttpHost("192.168.137.1:808");
     * hosts.add(host);
     *
     *    host = new HttpHost("192.168.137.1:809");
     *    hosts.add(host);
     *
     * host = new HttpHost("192.168.137.1:810");
     * hosts.add(host);
     * //将被动获取到的地址清单加入服务地址组report中
     * HttpProxyUtil.handleDiscoverHosts("schedule",hosts);
     */

    protected void poolChange(Properties properties ,String pool){
		boolean isdefault = pool == null || pool.equals("default");
		String hostsKey = null;
		String routingKey = null;
        String esHostKey = null;
		if(!isdefault){
			hostsKey = pool+".http.hosts";
			routingKey = pool+".http.routing";
            esHostKey = pool+".elasticsearch.rest.hostNames";
		}
		else{
			hostsKey = "default.http.hosts";
			routingKey = "default.http.routing";
            esHostKey = "default.elasticsearch.rest.hostNames";
		} 
 

        String _hosts = properties.getProperty(hostsKey);
        if(_hosts == null) {
            _hosts = properties.getProperty(defaultHostsKey);
        }
        if(_hosts == null){
            _hosts = properties.getProperty(esHostKey);
        }

        if(_hosts == null){
            _hosts = properties.getProperty(defaultEsHostsKey);
        }
        
        String _routing = properties.getProperty(routingKey);
        if(_routing == null){
            _routing = properties.getProperty(defaultRoutingKey);
        }
        //连通host和rounting一同更新
        handleDiscoverHosts(_hosts, pool,
                _routing);
 

	}
	
    @Override
    public void receiveConfigInfo(String configInfo) {
        try {
            if(logger.isInfoEnabled()) {
                logger.info("Changes for namespace {}", this.nacosPropertiesFilePlugin.getNamespace());
            }
            if(httpPools == null || httpPools.size() == 0){
                logger.info("Changes for httpPools ignored: httpPools is not setted yet.");
                return;
            }
            Properties properties = nacosPropertiesFilePlugin.getProperties(configInfo);
            for(String pool:httpPools) {
                poolChange(properties, pool);
            }
           
            
        } catch (IOException e) {
            throw new AssembleException(e);
        }
    }

	public Set<String> getHttpPools() {
		return httpPools;
	}

	@Override
	public void completeLoaded() {
		httpPools =  new TreeSet<String>();
		if(this.propertiesContainer != null){
			String poolNames = propertiesContainer.getProperty("http.poolNames");
			if(poolNames != null){

				String tmp[] = poolNames.split(",");
				for(int i = 0; i < tmp.length; i ++){
					this.httpPools.add(tmp[i].trim());
				}
			}
			else{
				this.httpPools.add("default");
			}
		}
	}
}
