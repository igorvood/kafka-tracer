package ru.vood.kafkatracer.request.meta.cache.dto

import org.springframework.kafka.listener.KafkaMessageListenerContainer
import ru.vood.kafkatracer.request.meta.dto.TopicJson

data class UserRequestListen(
    val listenTopics: ListenTopics,
    val topicListeners: Map<TopicJson, KafkaMessageListenerContainer<String, String>>,
    val messageKafka: MutableMap<String, KafkaData>
)
