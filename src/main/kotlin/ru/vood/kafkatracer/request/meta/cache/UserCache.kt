package ru.vood.kafkatracer.request.meta.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.configuration.KafkaListenerFactory
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.cache.dto.*
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvJson
import ru.vood.kafkatracer.request.meta.dto.TopicJson
import java.util.concurrent.TimeUnit

@Service
class UserCache(
    val req: Req,
    val kafkaListenerFactory: KafkaListenerFactory
) {

    private val logger: Logger = LoggerFactory.getLogger(UserCache::class.java)

    private final val loader = object : CacheLoader<RequestGraphDto, UserRequestListen>() {
        override fun load(key: RequestGraphDto): UserRequestListen {
            val requestGraph = requestGraph(key)

            val messageKafka = mutableMapOf<String, KafkaData>()

            val topicListeners = requestGraph.topics.associateWith {
                kafkaListenerFactory.messageListenerContainer(it.name, messageKafka)
            }

            return UserRequestListen(requestGraph, topicListeners, messageKafka)
        }
    }


    private final val listener = RemovalListener<RequestGraphDto, UserRequestListen> { n ->
        if (n.wasEvicted()) {
            logger.info("----delete user trace request ${n.key} cause ${n.cause}")
        }
    }

    val userCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
//        .expireAfterAccess(30, TimeUnit.SECONDS)
        .removalListener(listener)
        .build(loader)


    fun requestGraph(requestGraphDto: RequestGraphDto): ListenTopics {
        val traceArrows = when (val r = requestGraphDto) {
            is TopicRequestGraphDto -> req.arrowsByTopic(r.name)
            is FlinkSrvRequestGraphDto -> TODO()
        }
        val arrows = traceArrows.map {
            val graphNodeJson = when (val to = it.to) {
                is TopicJson -> to
                is FlinkSrvJson -> {
                    val from = it.from
                    if (from is TopicJson) {
                        from
                    } else throw java.lang.IllegalStateException("zsda")
                }

            }
            graphNodeJson
        }.toSet()

        return ListenTopics(arrows)
    }


}