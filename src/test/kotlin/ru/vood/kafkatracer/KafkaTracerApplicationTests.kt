package ru.vood.kafkatracer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ApplicationContext
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.rest.TracerRest
import java.util.concurrent.TimeUnit


@SpringBootTest(classes = [KafkaProducerTestConfig::class])
@DirtiesContext
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [ "listeners=PLAINTEXT://localhost:9092", "port=9092" ],
    topics = ["t1_from", "t1_To"])
class KafkaTracerApplicationTests {

    @MockkBean(relaxed = true)
    lateinit var restTemplateBuilder: RestTemplateBuilder

    @MockkBean(relaxed = true)
    lateinit var restTemplate: RestTemplate


    @Autowired
    lateinit var req: Req

    @Autowired
    lateinit var tracerRest: TracerRest

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>



    @Autowired
    lateinit var producer: ApplicationContext

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }
    @Test
    fun contextLoads() {
//        val beanDefinitionNames = producer.beanDefinitionNames
//        println(beanDefinitionNames)
//        println(beanDefinitionNames.filter { it.contains("prod", true) })
//
//        val bean = producer.getBean("kafkaProducerFactory", DefaultKafkaProducerFactory::class.java)
//
//        bean.

        every { restTemplate.getForObject(any<String>(), Array<JsonArrow>::class.java) } returns arrows
        every { restTemplateBuilder.build() } returns restTemplate

        val arrowsByGroup = runBlocking {
            val arrowsByGroup = tracerRest.arrowsByGroup("1")
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            arrowsByGroup
        }

        val f1: (String) -> ListenableFuture<SendResult<String, String>> = { a-> kafkaTemplate.send(t1FromName, a, "{}") }
        val f2: (String) -> ListenableFuture<SendResult<String, String>> = { a-> kafkaTemplate.send(t1ToName, a, "{}") }

        val sendList = listOf(f1, f2)
            .withIndex()
            .map { f -> f.value(f.index.toString()) }

        await()
            .atMost(10, TimeUnit.SECONDS)
            .until { sendList.filter { it.isDone }.size==sendList.size}


        await()
            .atMost(10, TimeUnit.SECONDS)
            .until { tracerRest.userCache.cache.get(RequestGraphDto("1")).messageKafka.size == sendList.size }
        val messageKafka = tracerRest.userCache.cache.get(RequestGraphDto("1")).messageKafka

        println(messageKafka)
    }



    companion object{
        val t1FromName = "t1_from"
        val t1ToName = "t1_To"

        val flinkSrvJson = ru.vood.kafkatracer.request.meta.dto.FlinkSrvJson("test_srv", "test_profile")
        val t1From = ru.vood.kafkatracer.request.meta.dto.TopicJson(t1FromName)

        val t1To = ru.vood.kafkatracer.request.meta.dto.TopicJson(t1ToName)

        val arrows = arrayOf<JsonArrow>(
            JsonArrow(null, t1From, flinkSrvJson, null),
            JsonArrow(flinkSrvJson, null, null, t1To),
        )
    }
}
