package qlaall.stateful

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import qlaall.util.SimpleDelayEventManager
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import javax.annotation.PostConstruct

/**
 * @author qlaall
 * @create 2020/2/20
 */
@Service
class ServerManager {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ServerManager::class.java)
    }

    @Autowired
    lateinit var serverInstanceService: ServerInstanceService
    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>
    @Value("\${aliyun.serverInstanceId}")
    lateinit var serverId:String

    @PostConstruct
    fun init(){
        SimpleDelayEventManager("SHUT_DOWN_SERVER", Consumer { serverInstanceService.stopServer(serverId) }, redisTemplate);
    }

    //todo 主要实现需求：

    fun setPlayerOnline(userName: String) {
        //用户统计
        val oldValue = redisTemplate.opsForValue().getAndSet("ONLINE_PLAYER:$userName", OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.ofHours(8)).toString())
        redisTemplate.expire("ONLINE_PLAYER:$userName",5,TimeUnit.MINUTES)
        if (oldValue==null){
            //初次上线开机
            logger.info("玩家$userName 上线，立刻开机")
            serverInstanceService.startServer(serverId)
        }else{
            //已在线忽略
        }
    }

    fun playerOffline(userName: String) {
        logger.info("$userName 下线了")
        redisTemplate.delete("ONLINE_PLAYER:$userName")
        val onlinePlayers = onlinePlayers()
        if (onlinePlayers.isEmpty()){
            logger.info("没有用户在线，5分钟后关机")
            SimpleDelayEventManager.NAMED_EVENT_MANAGER["SHUT_DOWN_SERVER"]!!.add(5,ChronoUnit.MINUTES)
        }
    }

    fun onlinePlayers():Set<String>{
        val keys = redisTemplate.keys("ONLINE_PLAYER:*")
        return keys
    }
}