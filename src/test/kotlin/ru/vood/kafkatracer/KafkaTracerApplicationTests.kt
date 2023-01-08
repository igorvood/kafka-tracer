package ru.vood.kafkatracer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ApplicationContext
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
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


        val arrowsByGroup = tracerRest.arrowsByGroup("1")
        Thread.sleep(1000)
        val sendList = (0 .. 9)
            .map { a-> kafkaTemplate.send(t1FromName, a.toString(), "{}") }

        await().atMost(10, TimeUnit.SECONDS)
            .until { sendList.filter { it.isDone }.size==10}


        val cache = tracerRest.userCache.cache.get(RequestGraphDto("1")).messageKafka.size

//        val send = producer.send(ProducerRecord(t1ToName, "asd", "asd"))

//        await().atMost(60, TimeUnit.SECONDS)
//            .until { send.isDone }


        await().atMost(10, TimeUnit.SECONDS)
            .until { tracerRest.userCache.cache.get(RequestGraphDto("1")).messageKafka.size != 0 }
        println(cache)
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
