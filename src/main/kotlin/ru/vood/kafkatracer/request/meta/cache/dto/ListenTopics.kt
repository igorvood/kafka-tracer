package ru.vood.kafkatracer.request.meta.cache.dto

import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TopicDto

data class ListenTopics(
    val topics: Set<TopicDto>,
    val traceArrows: Set<JsonArrow>
)
