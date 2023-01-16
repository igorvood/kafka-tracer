package ru.vood.kafkatracer

import arrow.core.Either
import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions
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
import ru.vood.kafkatracer.request.meta.ArrowsRepository
import ru.vood.kafkatracer.request.meta.cache.RequestCache
import ru.vood.kafkatracer.request.meta.cache.TopicCache
import ru.vood.kafkatracer.request.meta.cache.dto.Identity
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
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"],
    topics = ["t1_from", "t1_To"]
)
class KafkaTracerApplicationTests {

    @MockkBean(relaxed = true)
    lateinit var restTemplateBuilder: RestTemplateBuilder

    @MockkBean(relaxed = true)
    lateinit var restTemplate: RestTemplate


    @Autowired
    lateinit var req: ArrowsRepository

    @Autowired
    lateinit var topicCache: TopicCache


    @Autowired
    lateinit var tracerRest: TracerRest

    @Autowired
    lateinit var requestCache: RequestCache


    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>


    @Autowired
    lateinit var producer: ApplicationContext

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun contextLoads() {
        every { restTemplate.getForObject(any<String>(), String::class.java) } returns arrowsJsonStr
        every { restTemplateBuilder.build() } returns restTemplate

        val result = (1..1000)
            .map {
                Either.catch {
                    val groupId = it.toString()
                    val arrowsByGroup = runBlocking {
                        val arrowsByGroup = tracerRest.arrowsByGroup(groupId)
                        withContext(Dispatchers.IO) {
                            Thread.sleep(1000)
                        }
                        arrowsByGroup
                    }

                    val f1: (String) -> ListenableFuture<SendResult<String, String>> =
                        { a -> kafkaTemplate.send(t1FromName, a, "{}") }
                    val f2: (String) -> ListenableFuture<SendResult<String, String>> =
                        { a -> kafkaTemplate.send(t1ToName, a, "{}") }

                    val sendList = listOf(f1, f2)
                        .withIndex()
                        .map { f -> f.value(f.index.toString()) }

                    await()
                        .atMost(10, TimeUnit.SECONDS)
                        .until { sendList.filter { it.isDone }.size == sendList.size }


                    await()
                        .atMost(10, TimeUnit.SECONDS)
                        .until { requestCache.cache.get(RequestGraphDto("1")).topicListeners.size == sendList.size }
                    val groupCacheData = requestCache.cache.get(RequestGraphDto("1"))
                    val messageKafka = groupCacheData
                        .topicListeners.entries.map { it.value.lastKafkaMessage.get() }

                    assert(messageKafka.map { it?.identity }.contains(Identity()))
//                    val requestGraphDto = RequestGraphDto(groupId)
                    topicCache.cache.invalidateAll()
//                    requestCache.cache.invalidate(requestGraphDto)
                }
            }

        val okResult = result.filterIsInstance<Either.Right<Unit>>()
        if (okResult.size != result.size) {
            val errorResult = result.filterIsInstance<Either.Left<Throwable>>()
            val joinToString = errorResult
                .map { it.value.message }
                .joinToString("\n")


            Assertions.assertNull("Expected ${okResult.size} success, but ${errorResult.size} is error\n" + joinToString)
        }


    }


    companion object {
        val t1FromName = "t1_from"
        val t1ToName = "t1_To"

        val flinkSrvDto = ru.vood.kafkatracer.request.meta.dto.FlinkSrvDto("test_srv", "test_profile")
        val t1From = ru.vood.kafkatracer.request.meta.dto.TopicDto(t1FromName)

        val t1To = ru.vood.kafkatracer.request.meta.dto.TopicDto(t1ToName)

        val arrows = arrayOf(
            JsonArrow(t1From, flinkSrvDto),
            JsonArrow(flinkSrvDto, t1To),
        ).toSet()

        val arrowsJsonStr = Json.encodeToString(SetSerializer(JsonArrow.serializer()), arrows)

    }
}
