package ru.vood.kafkatracer.request.meta.cache.dto

import org.springframework.kafka.listener.AbstractMessageListenerContainer
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import java.util.concurrent.ConcurrentHashMap

data class UserRequestListen(
    val listenTopics: ListenTopics,
    val topicListeners: Map<TopicDto, AbstractMessageListenerContainer<*, *>>,
    val messageKafka: ConcurrentHashMap<String, KafkaData>
)
