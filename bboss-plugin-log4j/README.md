# log4j扩展插件
实现log4j日志文件向前滚动，当前文件的名称也随着索引文件的滚动而向前滚动
滚动的文件号有两种模式：
整数索引号，例如：xxx.log.1
日期索引号(yyyyMMddHHmmss)，例如:xxx.log.20170624202304

配置样例：useDatePattern用来切换两种模式 true 日期索引号 false 整数索引号（默认值），必须放置在file属性前面定义，

``
<appender name="invoke_log4j" class="org.apache.log4j.NormalRollingFileAppender">
		<param name="useDatePattern" value="true" />
		<param name="file" value="test.log" />
		<param name="append" value="true" />
		<param name="maxFileSize" value="10MB" />
		<param name="MaxBackupIndex" value="10"/>
		<param name="BufferedIO" value="false" />
		<param name="BufferSize" value="8192" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="[%d{dd/MM/yy HH:mm:ss:sss z}] %t %5p %c{2}: %m%n" />
		</layout>
	</appender>
``