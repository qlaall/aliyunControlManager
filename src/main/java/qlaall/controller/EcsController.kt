package qlaall.springAndKotlin.controller

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse
import com.aliyuncs.ecs.model.v20140526.StartInstanceResponse
import com.aliyuncs.ecs.model.v20140526.StopInstanceResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qlaall.service.EcsAgentService

@RestController
@RequestMapping("/ecs")
class EcsController {
    @Autowired
    lateinit var ecsAgentService: EcsAgentService

    @GetMapping("list")
    fun ecsList(): DescribeInstancesResponse {
        return ecsAgentService.getAllInstances()
    }

    @PostMapping("stop/{instanceId}")
    fun stopInstance(@PathVariable("instanceId") instanceId: String): StopInstanceResponse {
        return ecsAgentService.stopInstanceByEcsId(instanceId)
    }

    @PostMapping("start/{instanceId}")
    fun startInstance(@PathVariable("instanceId") instanceId: String): StartInstanceResponse {
        return ecsAgentService.startInstanceByEcsId(instanceId)
    }
}