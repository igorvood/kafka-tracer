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
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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


            val messageKafka = ConcurrentHashMap<String, KafkaData>()

            val topicListeners = requestGraph.topics.associateWith { topic ->
                kafkaListenerFactory.messageListenerContainer(topic.name) { km ->
                    val prev = messageKafka.put(topic.name, km)
                    logger.info("""last msg ${Date(km.timestamp)} topic ${topic.name}, prev msg ${prev?.timestamp}""")
                }
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

    fun  oldKafkaData(grpId: String, topicName: String) = cache[RequestGraphDto(grpId)].messageKafka//[topicName]


    fun requestGraph(requestGraphDto: RequestGraphDto): ListenTopics {

        val traceArrows = req.arrowsByTopic(requestGraphDto.groupId)

        val topics = traceArrows.map {
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

        return ListenTopics(topics, traceArrows)
    }


}