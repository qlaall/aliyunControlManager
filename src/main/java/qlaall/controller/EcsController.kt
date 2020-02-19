package qlaall.springAndKotlin.controller

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse
import com.aliyuncs.ecs.model.v20140526.StartInstanceResponse
import com.aliyuncs.ecs.model.v20140526.StopInstanceResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qlaall.service.EcsAgentService
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@RestController
@RequestMapping("/ecs")
class EcsController {
    @Autowired
    lateinit var ecsAgentService: EcsAgentService
    @GetMapping("detail")
    //将func的name设置一下
    fun setFuncName(): DescribeInstancesResponse {
       return ecsAgentService.getAllInstances()
    }

    @GetMapping("stop/{instanceId}")
    fun stopInstance(@PathVariable("instanceId")instanceId:String): StopInstanceResponse {
        return ecsAgentService.stopInstanceByEcsId(instanceId)
    }

    @GetMapping("start/{instanceId}")
    fun startInstance(@PathVariable("instanceId")instanceId:String): StartInstanceResponse {
        return ecsAgentService.startInstanceByEcsId(instanceId)
    }
}