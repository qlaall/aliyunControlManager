package qlaall

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping

@SpringBootApplication
class springAndKotlinServerApplication

fun main(args: Array<String>) {
	runApplication<springAndKotlinServerApplication>(*args)
}
