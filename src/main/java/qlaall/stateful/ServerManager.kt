package qlaall.stateful

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

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

    /**
     * 玩家在线trigger，初次上线时打日志。
     */
    fun setPlayerOnline(userName: String) {
        //用户统计
        val oldValue = redisTemplate.opsForValue().getAndSet("ONLINE_PLAYER:$userName", OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.ofHours(8)).toString())
        redisTemplate.expire("ONLINE_PLAYER:$userName",5,TimeUnit.MINUTES)
        if (oldValue==null){
            //初次上线打日志
            logger.info("玩家$userName 上线，立刻开机")
        }
        serverInstanceService.startServerIfNotRunning(serverId)
    }

    /**
     * 玩家下线trigger，打日志
     */
    fun playerOffline(userName: String) {
        logger.info("$userName 下线了")
        redisTemplate.delete("ONLINE_PLAYER:$userName")
    }

    fun onlinePlayers():Set<String>{
        val keys = redisTemplate.keys("ONLINE_PLAYER:*")
        return keys
    }
}