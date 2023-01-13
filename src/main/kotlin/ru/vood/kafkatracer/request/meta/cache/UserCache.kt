package ru.vood.kafkatracer.request.meta.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData
import ru.vood.kafkatracer.request.meta.cache.dto.ListenTopics
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.cache.dto.UserRequestListen
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvDto
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service
class UserCache(
    val req: Req,
    val cnsFactory: ConsumerFactory<String, String>,
) {

    private val logger: Logger = LoggerFactory.getLogger(UserCache::class.java)

    @Lookup
    fun messageListenerContainer(
        topic: String,
        messageApplyFun: (KafkaData) -> Unit,
        cnsFactory: ConsumerFactory<String, String>
    ): AbstractMessageListenerContainer<String, String> {
        error("must be implemented by Spring")
    }

    val processKafkaMessage: (String, ConcurrentHashMap<String, KafkaData>) -> ((KafkaData) -> Unit) =
        { topicName, messageKafkaMap ->
            val process: (KafkaData) -> Unit = { km ->
                val prev = messageKafkaMap.put(topicName, km)
                logger.info("""last msg ${Date(km.timestamp)} topic ${topicName}, prev msg ${prev?.timestamp}""")
            }
            process
        }


    private final val loader = object : CacheLoader<RequestGraphDto, UserRequestListen>() {
        override fun load(key: RequestGraphDto): UserRequestListen {
            val requestGraph = requestGraph(key)

            val messageKafka = ConcurrentHashMap<String, KafkaData>()

            val topicListeners: Map<TopicDto, AbstractMessageListenerContainer<String, String>> =
                requestGraph.topics
                    .associateWith { topic ->
                        val messageApplyFun = processKafkaMessage(topic.name, messageKafka)
                        messageListenerContainer(topic.name, messageApplyFun, cnsFactory)
                    }
            return UserRequestListen(requestGraph, topicListeners, messageKafka)
        }
    }


    private final val listener = RemovalListener<RequestGraphDto, UserRequestListen> { n ->
        if (n.wasEvicted()) {
            logger.info("----delete user trace request ${n.key} cause ${n.cause}")
        }
    }


    val cache = CacheBuilder
        .newBuilder()
        .expireAfterAccess(60, TimeUnit.MINUTES)
//        .expireAfterAccess(30, TimeUnit.SECONDS)
        .removalListener(listener)
        .build(loader)

    fun oldKafkaData(grpId: String, topicName: String) = cache[RequestGraphDto(grpId)].messageKafka//[topicName]


    fun requestGraph(requestGraphDto: RequestGraphDto): ListenTopics {

        val traceArrows = req.arrowsByTopic(requestGraphDto.groupId)

        val topics = traceArrows.map {
            val graphNodeJson = when (val to = it.to) {
                is TopicDto -> to
                is FlinkSrvDto -> {
                    val from = it.from
                    if (from is TopicDto) {
                        from
                    } else throw java.lang.IllegalStateException("zsda")
                }

            }
            graphNodeJson
        }.toSet()

        return ListenTopics(topics, traceArrows)
    }


}