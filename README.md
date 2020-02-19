
用包年包月的机器实在是有点贵，准备换成按量收费的机器，client启动时先调用这边开机，等client启动完了，server端基本上也差不多了，这样应该可以节省大量费用，但是得在client端动动手

编译：
mvn kotlin:compile conpile package

运行时需要添加两个阿里云的环境变量：
ACCESS_KEY=xxx;SECRET_KEY=yyy

