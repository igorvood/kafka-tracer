package ru.vood.kafkatracer.request.meta.cache.dto

import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import ru.vood.kafkatracer.request.meta.dto.TopicJson
import java.util.concurrent.ConcurrentHashMap

data class UserRequestListen(
    val listenTopics: ListenTopics,
    val topicListeners: Map<TopicJson, AbstractMessageListenerContainer<String, String>>,
    val messageKafka: ConcurrentHashMap<String, KafkaData>
)
