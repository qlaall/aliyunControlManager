package qlaall.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import qlaall.base.BizException
import java.time.Duration
import java.time.LocalDateTime


@Service
class HttpService {
    companion object{
        val logger=LoggerFactory.getLogger(HttpService::class.java)
    }
    @Autowired
    lateinit var restTemplate:RestTemplate

    fun <T> request(method: HttpMethod, url: String, body: (MultiValueMap<String, Any>) -> MultiValueMap<String, Any>, responseType: Class<T>?): T? {
        val headers = HttpHeaders()
        headers.add("Accept", MediaType.APPLICATION_JSON.toString())
//        headers.add("sysToken", "联通业务系统token")
        headers.contentType = MediaType.parseMediaType("multipart/form-data; charset=UTF-8")
        val formData = body(LinkedMultiValueMap())
        val requestEntity = HttpEntity<Any?>(formData, headers)
        val responseT = responseType ?: MutableMap::class.java
        val responseEntity = when (method) {
            HttpMethod.POST -> post(url, requestEntity, responseT)
            else -> exchange(url, method, requestEntity, responseT)
        }
        return responseEntity as T?
    }
    fun <T> request(method: HttpMethod, url: String, body: Any, responseType: Class<T>?): T? {
        val headers = HttpHeaders()
        headers.add("Accept", MediaType.APPLICATION_JSON.toString())
        headers.add("sysToken", "联通业务系统token")
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val requestEntity = HttpEntity(body,headers)
        val responseT = responseType ?: MutableMap::class.java
        val responseEntity = when (method) {
            HttpMethod.POST -> post(url, requestEntity, responseT)
            else -> throw BizException("not support")
        }
        return responseEntity as T?
    }
    private fun <T> post(url: String, requestEntity: HttpEntity<*>?, responseClass: Class<T>): T? {
        val n = LocalDateTime.now()
        try {
            val response: ResponseEntity<String> = restTemplate.postForEntity(url, requestEntity, String::class.java)
            logger.info("接口响应时间" + Duration.between(LocalDateTime.now(), n).abs().getSeconds())
            if (response.statusCode.is2xxSuccessful) {
                val body = response.body
                if (responseClass == String::class.java) {
                    return body as T?
                }
                try {
                    val mapper = ObjectMapper()
                    return mapper.readValue(body, responseClass)
                } catch (e: Exception) {
                    logger.error("接口转码错误,返回：{}", body)
                }
            }
        } catch (e: InternalServerError) {
            val error = e.responseBodyAsString
            logger.error("调用接口错误,返回：{}", error)
            throw BizException(error)
        } finally {
            logger.info("接口响应时间" + Duration.between(LocalDateTime.now(), n).abs().getSeconds())
        }
        throw BizException("数据调取失败，请重试")
    }

    private fun <T> exchange(url: String, method: HttpMethod, requestEntity: HttpEntity<*>?, responseClass: Class<T>): T? {
        val n = LocalDateTime.now()
        try {
            val response: ResponseEntity<String> = restTemplate.exchange(url, method, requestEntity, String::class.java)
            if (response.statusCode.is2xxSuccessful) {
                val body = response.body
                if (responseClass == String::class.java) {
                    return body as T?
                }
                try {
                    val mapper = ObjectMapper()
                    return mapper.readValue(body, responseClass)
                } catch (e: Exception) {
                    logger.error("接口转码错误,返回：{}", body)
                }
            }
        } catch (e: InternalServerError) {
            val error = e.responseBodyAsString
            logger.error("调用接口错误,返回：{}", error)
            throw BizException(error)
        } catch (e:HttpStatusCodeException){
            val error = e.responseBodyAsString
            logger.error("调用接口错误,返回：{}", error)
        }finally {
            logger.info("接口响应时间" + Duration.between(LocalDateTime.now(), n).abs().getSeconds())
        }
        throw BizException("数据调取失败，请重试")
    }
}