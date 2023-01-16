package ru.vood.kafkatracer.request.meta.cache.dto

import org.springframework.kafka.listener.AbstractMessageListenerContainer
import ru.vood.kafkatracer.request.meta.cache.TopicCacheValue
import ru.vood.kafkatracer.request.meta.dto.GraphNodeDto
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import ru.vood.kafkatracer.request.meta.dto.TraceArrow
import java.util.concurrent.ConcurrentHashMap

data class UserRequestListen(
    val listenTopics: ListenTopics,
    val topicListeners: Map<TopicDto, AbstractMessageListenerContainer<*, *>>,
    val messageKafka: ConcurrentHashMap<String, KafkaData>
)

data class GroupRequestListen(
    val traceArrows: Set<TraceArrow<GraphNodeDto, GraphNodeDto>>,
    val topicListeners: Map<TopicDto, TopicCacheValue>,
) {
    val topicWithMessage by lazy { topicListeners.map { it.key to it.value.lastKafkaMessage } }
}
