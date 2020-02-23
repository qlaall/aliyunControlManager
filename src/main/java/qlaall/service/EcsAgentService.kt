package qlaall.service

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.ecs.model.v20140526.*
import com.aliyuncs.profile.DefaultProfile
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EcsAgentService {
    @Value("\${aliyun.accesskey}")
    lateinit var accessKey:String
    @Value("\${aliyun.secretkey}")
    lateinit var secretKey:String

    fun getAllInstances():DescribeInstancesResponse{
        val profile = DefaultProfile.getProfile("cn-zhangjiakou", accessKey, secretKey)
        val client = DefaultAcsClient(profile)
        return client.getAcsResponse(DescribeInstancesRequest())
    }
  
    fun stopInstanceByEcsId(instanceId:String):StopInstanceResponse {
        val profile = DefaultProfile.getProfile("cn-zhangjiakou", accessKey, secretKey)
        val client = DefaultAcsClient(profile)
        val s = StopInstanceRequest().apply {
            this.instanceId = instanceId
        }
        return client.getAcsResponse(s)
    }

    fun startInstanceByEcsId(instanceId: String):StartInstanceResponse {
        val profile = DefaultProfile.getProfile("cn-zhangjiakou", accessKey, secretKey)
        val client = DefaultAcsClient(profile)
        val s = StartInstanceRequest().apply {
            this.instanceId = instanceId
        }
        try {
            return client.getAcsResponse(s)
        } catch (e: Exception) {
            e.printStackTrace()
            Thread.sleep(50000L)
            return startInstanceByEcsId(instanceId)
        }
    }

}