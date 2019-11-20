package io.growing.other

/**
 * 匹配datayi.cn的https子域名
 * https://datayi.cn
 * https://\*.datayi.cn
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/28
 */
object RegexTest extends App {

  val regex = "^(https://)((?!-)([a-zA-Z0-9-]+(?<!-))\\.)?(datayi\\.cn)$"

  val true_domains = Seq("https://datayi.cn", "https://163.datayi.cn",
    "https://gio.datayi.cn", "https://12-6r.datayi.cn", "https://163.datayi.cn", "https://12-6-r.datayi.cn")

  val false_domains = Seq("http://datayi.cn", "https://.datayi.cn", "https://datayi.co",
    "datayi.cn", "https://-126r.datayi.cn", "https://126r-.datayi.cn")

  Console println "true: " + true_domains.filter(x => x.matches(regex))
  Console println "false: " + false_domains.filter(x => x.matches(regex))

}
