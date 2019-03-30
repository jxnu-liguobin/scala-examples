### Play framework Examples with Scala

>版本 

* Play 2.7 
* Scala 2.12

> 目前主要Examples

* 配置API
* 依赖注入
* http编程
* 异步HTTP编程
* 表格提交和验证
* Play-json
* 处理文件上传
* Akka集成
* Csrf拦截
* Logback日志配置
* Scala常用算子


PS:主要参考[官网](https://www.playframework.com/documentation/2.7.x/ScalaHome)


> 默认的Play项目结构

```
+---app                         Play Web 应用全部代码所在目录

|   +---services                业务逻辑代码所在目录

|   |   Counter.scala       

|   +---controllers             控制器代码所在目录

|   |   HomeController.scala    默认控制器代码

|   +---models                  模型实体类

|   |   User  

|   +---views                   视图（Play Scala HTML模板） 代码所在目录

|   |   main.scala.html     

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

---| Module.scala               Guice注入绑定配置

+---target                      存放编译后的可执行代码和编译时的中间代码
```