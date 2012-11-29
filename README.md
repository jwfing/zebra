##Zebra

### introduce 

zebra是一个网络爬虫的框架，包括如下几个部分：
1，spider    负责对特定种子页面进行定期刷新，以发现新的link；
2. silkworm  负责对新link进行下载、分析，以得到最终期望的数据；
3. kestrel   消息队列，负责在spider和silkworm之间传递数据;
后台数据主要结构如下：
1. 种子列表
2. 发现的新link集
3. 下载、分析出来的文章集
可用的工具有：

### requirement

zebra采用maven进行代码管理，主要依赖于如下几个模块：
1, spring / hibernate, SSH架构的基石；
2, httpclient，用来抓取实际的网页
3, camel, 在silkworm中负责消息路由

### compile & install

1，在开始编译之前，请在zebra根目录下运行env.init脚本，以将charsetdet/mmseg两个jar文件安装到本地maven repo。
2. 整个项目分为三个模块：zebra-common, zebra-spider, zebra-silkworm. 对于每一个模块都可以使用mvn工具进行编译，
   譬如：
   2.1 可以使用mvn eclipse:eclipse命令来生成可导入eclipse的工程文件。
       注意：初次编译的时候因为maven要下载大量的依赖库，所以耗时较长，请保持网络通畅且耐心等待。
   2.2 可以通过mvn package来打包jar文件。注意：
       还可以通过mvn package -Dmaven.test.skip=true来跳过单元测试代码。
   2.3 通过mvn dependency:copy-dependencies来拷贝所有依赖的jar包。
3，我们使用fabric工具来进行安装和发布。基本上我们只需要在项目根目录下执行如下三个命令即可完成所有服务的部署：
   3.1 fab deploy_kestrel -H {yourname}@{target-machine-hostname} 用来将kestrel部署到目标机器上. e.g.
       fab deploy_kestrel -H deploy@localhost
   3.2 fab deploy_spider -H {yourname}@{target-machine-hostname} 用来将spider部署到目标机器上. e.g.
       fab deploy_spider -H deploy@localhost
   3.2 fab deploy_silkworm -H {yourname}@{target-machine-hostname} 用来将silkworm部署到目标机器上. e.g.
       fab deploy_silkworm -H deploy@localhost
   部署到的目录位置默认是: /var/backends/kestrel /var/backends/zebra-spider /var/backends/zebra-silkworm
   注：关于fabric请参见http://docs.fabfile.org/en/1.5/ 获取详细信息。
4. 使用fabric部署可执行文件的同时，我们也会部署一个启停服务的脚本，放置在/etc/init.d/目录下。每个脚本都支持
   start/status/stop等指令。譬如我们可以：
   sudo /etc/init.d/kestrel start 来启动kestrel
   sudo /etc/init.d/zebra-spider start 来启动spider
   sudo /etc/init.d/zebra-silkworm start 来启动silkworm
5. 实际运行spider和silkworm之前，请仔细查看配置文件，改成适合自己的需求的配置（如Mysql配置，线程数量配置等等）。
