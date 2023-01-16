package ru.vood.kafkatracer.request.meta.cache.dto

import org.springframework.kafka.listener.AbstractMessageListenerContainer
import ru.vood.kafkatracer.request.meta.cache.TopicCacheValue
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import java.util.concurrent.ConcurrentHashMap

data class UserRequestListen(
    val listenTopics: ListenTopics,
    val topicListeners: Map<TopicDto, AbstractMessageListenerContainer<*, *>>,
    val messageKafka: ConcurrentHashMap<String, KafkaData>
)

data class GroupRequestListen(
    val traceArrows: Set<JsonArrow>,
    val topicListeners: Map<TopicDto, TopicCacheValue>,
) {
    val topicWithMessage by lazy { topicListeners.map { it.key to it.value.lastKafkaMessage } }
}
