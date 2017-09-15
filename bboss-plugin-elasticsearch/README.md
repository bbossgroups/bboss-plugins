# bboss elasticsearch插件

#elastic search配置-conf/elasticsearch.properties
##tcp 地址和端口配置，集群用逗号分隔：127.0.0.1:9300,127.0.0.1:9301,127.0.0.1:9302
elasticsearch.transport.hostNames=127.0.0.1:9300
##http地址和端口配置，集群用逗号分隔：127.0.0.1:9200,127.0.0.1:9201,127.0.0.1:9202
elasticsearch.rest.hostNames=127.0.0.1:9200
##每天产生的索引日期格式
elasticsearch.dateFormat=yyyy.MM.dd
#http连接池配置-conf/elasticsearch.properties
##总共允许的最大连接数
http.maxTotal = 400
##每个地址运行的最大连接数
http.defaultMaxPerRoute = 200
