### Play framework Examples with Scala

> Version

* Play 2.7 
* Scala 2.12

> Main concepts for Scala

* Configuration API
* HTTP programming
* Asynchronous HTTP programming
* Working with Json
* Handling file upload
* Form submission and validation
* Integrating with Akka
* Dependency injection
* Testing your application
* Logging
* Scala Common Operators
* CSRF configuration
* MySQL connection configure 
* Restful `crud` based on MySQL and Slick 
  
PS:Reference resources,but not all of them.[playframework](https://www.playframework.com/documentation/2.7.x/ScalaHome)

> Default Play project structure
  
```
+---app                         Play Web 应用全部代码所在目录

|   +---services                业务逻辑代码所在目录

|   +---controllers             控制器代码所在目录

|   +---models                  模型实体类

|   +---views                   视图（Play Scala HTML模板） 代码所在目录

|   +---actor                   存放actor

|   +---modules                 存放自定义module，注入配置

|   +---filters                 存放自定义filter

+---conf                        Play 配置文件所在目录

|   |   application.conf        应用配置文件

|   |   routes                  应用入口路由文件，所有的HTTP请求将通过该文件转发到指定的Scala对象处理

+---logs                        日志目录

|   |   application.log         应用运行日志

+---project                     SBT工程文件

|   |   build.properties        保存所需的SBT版本信息，通常无需更改

|   |   Build.scala             主要的工程配置文件

|   |   plugins.sbt             告知SBT本工程所需要的插件以及下载位置

+---public                      存储一切直接发送给浏览器的资源文件

|   +---images                  图像文件，如JPEG、PNG、GIF等

|   +---javascripts             JavaScript脚本文件

|   +---stylesheets             CSS样式表文件

---| Module.scala               默认Guice注入绑定配置

+---test                        存放测试相关代码

+---target                      存放编译后的可执行代码和编译时的中间代码

```