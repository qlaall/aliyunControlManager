package qlaall.stateful

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

/**
 * @author qlaall
 * @create 2020/2/20
 */

class ServerManager {
    companion object {
        val INSTANCE = ServerManager()
        val logger: Logger = LoggerFactory.getLogger(ServerManager::class.java)
    }

    //todo 主要实现需求：
    //无人在线后需要延迟关机，并可以取消关机命令
    //整点开关机
    //首次开机时间后N整小时需要关机
    //


    fun onlinePlayers(): MutableSet<String> {
        TODO("not implemented")
    }

    fun playerOnline(userName: String) {
        TODO("not implemented")
        logger.info("$userName 上线了")

    }

    fun playerOffline(userName: String) {
        TODO("not implemented")
        logger.info("$userName 下线了")
    }
}