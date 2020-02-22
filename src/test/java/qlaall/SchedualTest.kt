package qlaall

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import qlaall.stateful.EventDealer
import java.time.temporal.ChronoUnit
import java.util.function.Consumer

@ActiveProfiles("")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [SMApplication::class])
class SchedualTest {
	@Autowired
	lateinit var redisTemplate: RedisTemplate<String,String>
	@Test
	fun test1(){
		val tired = EventDealer("tired", Consumer { s -> println(s) }, redisTemplate)
		tired.add(3,ChronoUnit.SECONDS)
		tired.add(9,ChronoUnit.SECONDS)
		tired.add(6,ChronoUnit.SECONDS)
		tired.add(12,ChronoUnit.SECONDS)
		val full = EventDealer("full", Consumer { s -> System.err.println(s) }, redisTemplate)
		full.add(5,ChronoUnit.SECONDS)
		full.add(15,ChronoUnit.SECONDS)
		full.add(25,ChronoUnit.SECONDS)

		Thread.sleep(60000L)
	}

}
