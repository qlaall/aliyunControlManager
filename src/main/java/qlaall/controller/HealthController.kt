package qlaall.springAndKotlin.controller

import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping("")
    fun setFuncName(){}

}