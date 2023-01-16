package ru.vood.kafkatracer.request.meta.cache.dto

import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TopicDto

data class GroupRequestListen(
    val traceArrows: Set<JsonArrow>,
    val topicListeners: Map<TopicDto, TopicCacheValue>,
) {
    val topicWithMessage by lazy { topicListeners.map { it.key to it.value.lastKafkaMessage } }
}
