# bboss 插件工程

bboss-plugins project.包含kafka、log4j、spring、dubbo、activemq、word转pdf、pdf转swf项目，持久层hibernate/hibernate4插件

# 版本构建方法

gradle clean publishToMavenLocal

需要通过gradle构建发布版本,gradle安装配置参考文档：

https://esdoc.bbossgroups.com/#/bboss-build



然后运行以下指令,启动tomcat和demo应用

gradle :bboss-plugin-wordpdf-web:tomcatStart

启动后可以在浏览器端访问以下地址：

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdfswftool.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdfswf.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdf.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/word.jsp

http://localhost/bboss-plugin-wordpdf-web/FlexPaper_2.0.3/index_ooo.html


注意：运行demo工程前，还需要安装liferoffice和swftool并启动soffice进程，安装方法请参考文档:[/bboss-plugin-wordpdf/文档转换部署文档.doc],

安装完毕后，修改配置文件/bboss-plugins/bboss-plugin-wordpdf-web/WebRoot/WEB-INF/bboss-wordpdf.xml中相关属性对应路径swftoolWorkDir(swftool安装目录)、officeHome(libreoffice安装目录)、templatedir(word模板所在目录),resultdir(转换文档存放目录)：

```
<properties>
    <property name="/wordpdf/*.page"    		
		f:flashpaperWorkDir="D:\FlashPaper\FlashPaper2.2\"   
		f:templatedir="D:/d/workspace/bbossgroups/bboss-plugins/bboss-plugin-wordpdf"
		f:swftoolWorkDir="c:/environment/SWFTools/" 	
		f:officeHome = "c:/environment/LibreOffice 5"	
		f:resultdir="d:/test"
    	class="org.frameworkset.web.wordpdf.NewPrinterController"/>
</properties>
```



# 联系我们

**技术交流群：21220580,166471282**

<img src="https://esdoc.bbossgroups.com/images/qrcode.jpg"  height="200" width="200"><img src="https://esdoc.bbossgroups.com/images/douyin.png"  height="200" width="200"><img src="https://esdoc.bbossgroups.com/images/wvidio.png"  height="200" width="200">


# 支持我们

如果您正在使用bboss，或是想支持我们继续开发，您可以通过如下方式支持我们：

1.Star并向您的朋友推荐或分享

[bboss elasticsearch client](https://gitee.com/bboss/bboss-elastic)🚀

[数据采集&流批一体化处理](https://gitee.com/bboss/bboss-elastic-tran)🚀

2.通过[爱发电 ](https://afdian.net/a/bbossgroups)直接捐赠，或者扫描下面二维码进行一次性捐款赞助，请作者喝一杯咖啡☕️

<img src="https://esdoc.bbossgroups.com/images/alipay.png"  height="200" width="200">

<img src="https://esdoc.bbossgroups.com/images/wchat.png"   height="200" width="200" />

非常感谢您对开源精神的支持！❤您的捐赠将用于bboss社区建设、QQ群年费、网站云服务器租赁费用。


## License

The BBoss Framework is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

# Star History

[![Star History Chart](https://api.star-history.com/svg?repos=bbossgroups/bboss-plugins&type=Date)](https://star-history.com/#bbossgroups/bboss-plugins&Date)

