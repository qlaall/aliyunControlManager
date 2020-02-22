
用包年包月的机器实在是有点贵，准备换成按量收费的机器，client启动时先调用这边开机，等client启动完了，server端基本上也差不多了，这样应该可以节省大量费用，但是得在client端动动手

编译：
mvn kotlin:compile conpile package

环境变量解释
ACCESS_KEY=aliyun具有ECS权限的子用户的accessKey
SECRET_KEY=accessKey对应的secretKey
SERVER_INSTANCE_ID=具体服务的管理主机
REDIS_HOST=redis的Host (可选，默认localhost)
REDIS_PORT=redis的port (可选，默认6379)
