package qlaall.service

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.ecs.model.v20140526.*
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.profile.DefaultProfile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EcsAgentService {
    companion object{
        val logger=LoggerFactory.getLogger(EcsAgentService::class.java)
    }
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
        logger.info("向$instanceId 服务器发送关机信号。")
        return client.getAcsResponse(s)
    }

    fun startInstanceByEcsId(instanceId: String):StartInstanceResponse {
        val profile = DefaultProfile.getProfile("cn-zhangjiakou", accessKey, secretKey)
        val client = DefaultAcsClient(profile)
        val s = StartInstanceRequest().apply {
            this.instanceId = instanceId
        }
        try {
            logger.info("向$instanceId 服务器发送开机信号。")
            return client.getAcsResponse(s)
        } catch (e:ClientException){
            if (e.errMsg=="The specified instance is in an incorrect status for the requested action; Status of the specified instance is Running but the expected status is in (Stopped)."){
                //这说明已经正常启动了，所以直接return就可以
                return StartInstanceResponse()
            }else{
                throw e;
            }
        }catch (e: Exception) {
            e.printStackTrace()
            Thread.sleep(5000L)
            return startInstanceByEcsId(instanceId)
        }
    }

}