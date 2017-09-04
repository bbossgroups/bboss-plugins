package org.frameworkset.elasticsearch.client;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ResponseHandler;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.IndexNameBuilder;
import org.frameworkset.elasticsearch.event.Event;
import org.frameworkset.elasticsearch.template.ESInfo;
import org.frameworkset.elasticsearch.template.ESTemplate;
import org.frameworkset.elasticsearch.template.ESUtil;
import org.frameworkset.spi.assemble.Param;
import org.frameworkset.util.ClassUtil;
import org.frameworkset.util.ClassUtil.ClassInfo;
import org.frameworkset.util.ClassUtil.PropertieDescription;
import org.frameworkset.util.annotations.DateFormateMeta;
import org.frameworkset.util.annotations.wraper.ColumnWraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.common.poolman.NestedSQLException;
import com.frameworkset.util.ColumnEditorInf;
import com.frameworkset.util.ColumnToFieldEditor;
import com.frameworkset.util.ColumnType;

import bboss.org.apache.velocity.VelocityContext;

/**
 * 通过配置文件加载模板
 */
public class ConfigRestClientUtil extends RestClientUtil {
	private static Logger logger = LoggerFactory.getLogger(ConfigRestClientUtil.class);
	
	private String configFile;
	private ESUtil esUtil = null;

	public ConfigRestClientUtil(ElasticSearchClient client, IndexNameBuilder indexNameBuilder, String configFile) {
		super(client, indexNameBuilder);
		this.configFile = configFile;
		this.esUtil = ESUtil.getInstance(configFile);
	}

	public void addEvent(Event event, ElasticSearchEventSerializer elasticSearchEventSerializer) throws ElasticSearchException {
		if (bulkBuilder == null) {
			bulkBuilder = new StringBuilder();
		}
		client.createIndexRequest(bulkBuilder, indexNameBuilder, event, elasticSearchEventSerializer);

	}

	@Override
	public void updateIndexs(Event event, ElasticSearchEventSerializer elasticSearchEventSerializer) throws ElasticSearchException {

	}

	/**
	 * @param path
	 * @return
	 */
	public Object executeRequest(String path) throws ElasticSearchException {
		return executeRequest(path, null);
	}

	public Object execute() throws ElasticSearchException {
		return client.execute(this.bulkBuilder.toString());
	}

	@Override
	public Client getClient() {
		return null;
	}

	@Override
	public void deleteIndex(String indexName, String indexType, String... ids) throws ElasticSearchException {
		// TODO Auto-generated method stub

	}
	protected  VelocityContext buildVelocityContext(Object bean)
    {
		VelocityContext context_ = new VelocityContext();
		ClassInfo beanInfo = ClassUtil.getClassInfo(bean.getClass());		 
		String name = null;
		DateFormateMeta dataformat = null;
 
//		String charset = null;
		Object value =  null;
		Class type = null;
	
//		Method writeMethod = null;
		 
		List<PropertieDescription> attributes = beanInfo.getPropertyDescriptors();
		for(int i = 0; attributes != null && i < attributes.size();i ++ )
		{
			PropertieDescription property = attributes.get(i);
			ColumnWraper column = property.getColumn();
			if(column!= null && (column.ignoreCUDbind() || column.ignorebind()))
				continue;

			type = property.getPropertyType();

			
			try {
				if(property.canread())
				{	
					try {						
						value =  property.getValue(bean);
					}
					catch(InvocationTargetException e1)
					{
						logger.error("获取属性["+beanInfo.getClazz().getName()+"."+property.getName()+"]值失败：",e1.getTargetException());
					} catch (Exception e1) {
						logger.error("获取属性["+beanInfo.getClazz().getName()+"."+property.getName()+"]值失败：",e1);
					}

					name = property.getName();
					
					if(column != null)
					{
						ColumnEditorInf editor = column.editor();
						if(editor == null || editor instanceof ColumnToFieldEditor)
						{

							dataformat = column.getDateFormateMeta();
							 
//							charset = column.charset();
							
							
						}
						else
						{	
							Object cv = editor.toColumnValue(column, value);
							if(cv == null)
								throw new NestedSQLException("转换属性["+beanInfo.getClazz().getName()+"."+property.getName()+"]值失败：值为null时，转换器必须返回ColumnType类型的对象,用来指示表字段对应的java类型。");
							 
							if(!(cv instanceof ColumnType))
							{
								value = cv;
								type = value.getClass();
								
							}
							else
							{
								type = ((ColumnType)cv).getType();
							}
						}
						
					}
					if(value == null){
						context_.put(name, null);
					}
					else if(dataformat != null && value instanceof Date){
						context_.put(name, this.getDate((Date)value, dataformat));
					}
					else
					{
						context_.put(name, value);
					}
//					params.addSQLParamWithDateFormateMeta(name, value, sqltype, dataformat,charset);
					
				}
				name = null; value = null;  
				dataformat = null;
				 
//				charset = null;
				
				
			} catch (SecurityException e) {
				throw new ElasticSearchException(e);
			} catch (IllegalArgumentException e) {
				throw new ElasticSearchException(e);
			}
//			catch (InvocationTargetException e) {
//				throw new ElasticSearchException(e.getTargetException());
//			}
		
	
			catch (Exception e) {
				throw new ElasticSearchException(e);
			} 

			
		}
    	
		
		 
    	return context_;
    	
    }
	
//	public String to_char(String date,String format)
//    {
//    	 SimpleDateFormat f = new SimpleDateFormat(format);
//    	 return f.format(SimpleStringUtil.stringToDate(date,format));
//	         
//    }
//    
//    public String to_char(String date)
//    {
//    	return to_char(date,this.FORMART_ALL);
//	    
//    }
	private static String java_date_format = "yyyy-MM-dd HH:mm:ss";
	 public String getJavaDateFormat()
	    {
	        return java_date_format;
	    }
    public String getDate(Date date,DateFormateMeta  dateFormateMeta) throws ParseException
    {
    	String format = null;
        if(dateFormateMeta == null )
        {
            format = this.getJavaDateFormat();
        }
        else
        	format = dateFormateMeta.getDateformat();
        SimpleDateFormat f = dateFormateMeta == null?new SimpleDateFormat(format):new SimpleDateFormat(format,dateFormateMeta.getLocale());
        String _date = f.format(date);
        
        return _date;
    }
    
   
	
	protected  VelocityContext buildVelocityContext(Map data)
    {   	
    	
		VelocityContext context_ = new VelocityContext();		 
		Iterator<Entry<String, Param>> it = data.entrySet().iterator();
		Object temp = null;
		while(it.hasNext())
		{
			Entry<String, Param> entry = it.next();
			temp = entry.getValue();

			if(temp != null)
				context_.put(entry.getKey(), temp);
		}		 
	
    	return context_;
    	
    }
	private String evalTemplate(String templateName,Map params){
		
		ESInfo esInfo = esUtil.getESInfo(templateName);
		if(esInfo == null)
			return templateName;
		if(params == null || params.size() == 0)
			return esInfo.getTemplate();
		String template  = null;
		if(esInfo.isTpl())
    	{
			ESTemplate esTemplate = esInfo.getEstpl();
			esTemplate.process();//识别sql语句是不是真正的velocity sql模板
    		if(esInfo.isTpl())
    		{
    			VelocityContext vcontext = buildVelocityContext(params);//一个context是否可以被同时用于多次运算呢？
		    	
		    	StringWriter sw = new StringWriter();
		    	esTemplate.merge(vcontext,sw);
		    	template = sw.toString();
    		}
    		else
    		{
    			template = esInfo.getTemplate();
    		}
	    	
    	}
    	else
    	{
    		template = esInfo.getTemplate();
    	}
		 
		return template;
		//return templateName;
	}
	
	private String evalTemplate(String templateName,Object params){
		
		ESInfo esInfo = esUtil.getESInfo(templateName);
		if(esInfo == null)
			return templateName;
		if(params == null )
			return esInfo.getTemplate();
		String template  = null;
		if(esInfo.isTpl())
    	{
			esInfo.getEstpl().process();//识别sql语句是不是真正的velocity sql模板
    		if(esInfo.isTpl())
    		{
    			VelocityContext vcontext = buildVelocityContext(params);//一个context是否可以被同时用于多次运算呢？
		    	
		    	StringWriter sw = new StringWriter();
		    	esInfo.getEstpl().merge(vcontext,sw);
		    	template = sw.toString();
    		}
    		else
    		{
    			template = esInfo.getTemplate();
    		}
	    	
    	}
    	else
    	{
    		template = esInfo.getTemplate();
    	}
		 
		return template;
		//return templateName;
	}
	
	
	@Override
	public String executeRequest(String path, String templateName) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path, evalTemplate(templateName,(Map)null));
	}
	@Override
	public String executeRequest(String path, String templateName,Map params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path, evalTemplate(templateName,params));
	}
	
	@Override
	public String executeRequest(String path, String templateName,Object params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path, evalTemplate(templateName,params));
	}
	
	public <T> T executeRequest(String path, String templateName,Map params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
		return this.client.executeRequest(path, evalTemplate(templateName,params), responseHandler);
	}
	
	 
	public <T> T  executeRequest(String path, String templateName,Object params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
		return this.client.executeRequest(path, evalTemplate(templateName,params), responseHandler);
	}

	@Override
	public String delete(String path, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String executeHttp(String path, String templateName,String action) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeHttp(path,evalTemplate(templateName,null));
	}
	
	
	 
	
	@Override
	public <T> T executeRequest(String path, String templateName,ResponseHandler<T> responseHandler) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path,evalTemplate(templateName,null),  responseHandler);
	}
	@Override
	public SearchResult search(String path, String templateName, Map params) throws ElasticSearchException {
		return super.executeRequest(  path,   evalTemplate(templateName,params),new ElasticSearchResponseHandler());
	}
	@Override
	public SearchResult search(String path, String templateName, Object params) throws ElasticSearchException {
		return super.executeRequest(  path,   evalTemplate(templateName,params),new ElasticSearchResponseHandler());
	}
	@Override
	public SearchResult search(String path, String templateName) throws ElasticSearchException {
		 
		return super.executeRequest(  path,   evalTemplate(templateName,null),new ElasticSearchResponseHandler());
	} 
	
	
	/**
	  * 更新索引定义
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String updateIndiceMapping(String action,String templateName)  throws ElasticSearchException {
		 return super.updateIndiceMapping(action,evalTemplate(templateName,null));
	 }
	 
	 /**
	  * 创建索引定义
	  * curl -XPUT 'localhost:9200/test?pretty' -H 'Content-Type: application/json' -d'
		{
		    "settings" : {
		        "number_of_shards" : 1
		    },
		    "mappings" : {
		        "type1" : {
		            "properties" : {
		                "field1" : { "type" : "text" }
		            }
		        }
		    }
		}
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String createIndiceMapping(String indexName,String templateName)  throws ElasticSearchException {
		 return super.createIndiceMapping(indexName,evalTemplate(templateName,null));
	 }
	 
	 
	 /**
	  * 更新索引定义
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String updateIndiceMapping(String action,String templateName,Object parameter)  throws ElasticSearchException {
		 return super.updateIndiceMapping(action,evalTemplate(templateName,parameter));
	 }
	 
	 /**
	  * 创建索引定义
	  * curl -XPUT 'localhost:9200/test?pretty' -H 'Content-Type: application/json' -d'
		{
		    "settings" : {
		        "number_of_shards" : 1
		    },
		    "mappings" : {
		        "type1" : {
		            "properties" : {
		                "field1" : { "type" : "text" }
		            }
		        }
		    }
		}
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String createIndiceMapping(String indexName,String templateName,Object parameter)  throws ElasticSearchException {
		 return super.createIndiceMapping(indexName,evalTemplate(templateName,parameter));
	 }
	 
	 /**
	  * 更新索引定义
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String updateIndiceMapping(String action,String templateName,Map parameter)  throws ElasticSearchException {
		 return super.updateIndiceMapping(action,evalTemplate(templateName,parameter));
	 }
	 
	 /**
	  * 创建索引定义
	  * curl -XPUT 'localhost:9200/test?pretty' -H 'Content-Type: application/json' -d'
		{
		    "settings" : {
		        "number_of_shards" : 1
		    },
		    "mappings" : {
		        "type1" : {
		            "properties" : {
		                "field1" : { "type" : "text" }
		            }
		        }
		    }
		}
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String createIndiceMapping(String indexName,String templateName,Map parameter)  throws ElasticSearchException {
		 return super.createIndiceMapping(indexName,evalTemplate(templateName,parameter));
	 }

}
