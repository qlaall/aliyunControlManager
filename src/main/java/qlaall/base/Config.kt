package qlaall.base

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

annotation class NoArgConstruction
@Configuration
class Config {
    @Bean
    fun restTemplate():RestTemplate{
        return RestTemplate()
    }
}
