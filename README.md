# bboss 插件工程
 
bboss-plugins project.包含word转pdf、pdf转swf项目，持久层hibernate插件

# bboss hibernate插件工程

hihernate插件包含以下两个功能：

1.通过bboss初始化hibernate并加载hibernate o/r mapping文件

2.hibernate延迟加载对象序列化插件

# bboss wordpdf插件工程

1.通过gretty gradle插件运行demo工程bboss-plugin-wordpdf-web

2.运行前先执行/bboss-plugins的install任务，编译构建所有插件模块：

cd bboss-plugins

gradle install

构建成功后，先启用gretty插件（注意：第一次构建工程，需要关闭gretty插件，默认关闭）

修改/bboss-plugins/gradle.properties中属性为true，即可启用插件：

enable_gretty=true

然后运行以下指令,启动tomcat和demo应用

gradle :bboss-plugin-wordpdf-web:tomcatStart

启动后可以在浏览器端访问以下地址：

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdfswftool.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdfswf.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/wordpdf.jsp

http://localhost/bboss-plugin-wordpdf-web/wordpdf/word.jsp

http://localhost/bboss-plugin-wordpdf-web/FlexPaper_2.0.3/index_ooo.html


注意：运行demo工程前，还需要安装liferoffice和swftool并启动soffice进程，安装方法请参考文档:[/bboss-plugin-wordpdf/文档转换部署文档.doc],

安装完毕后，修改配置文件/bboss-plugins/bboss-plugin-wordpdf-web/WebRoot/WEB-INF/bboss-wordpdf.xml中相关属性对应路径swftoolWorkDir(swftool安装目录)、officeHome(libreoffice安装目录)、templatedir(word模板所在目录)：

```
<properties>
    <property name="/wordpdf/*.page"    		
		f:flashpaperWorkDir="D:\FlashPaper\FlashPaper2.2\"   
		f:templatedir="D:/d/workspace/bbossgroups/bboss-plugins/bboss-plugin-wordpdf"
		f:swftoolWorkDir="c:/environment/SWFTools/" 	
		f:officeHome = "c:/environment/LibreOffice 5"	
    	class="org.frameworkset.web.wordpdf.NewPrinterController"/>
</properties>
```






## License

The BBoss Framework is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
