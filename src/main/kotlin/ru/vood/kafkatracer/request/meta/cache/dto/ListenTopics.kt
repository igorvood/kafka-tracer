package ru.vood.kafkatracer.request.meta.cache.dto

import ru.vood.kafkatracer.request.meta.dto.GraphNodeDto
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import ru.vood.kafkatracer.request.meta.dto.TraceArrow

data class ListenTopics(
    val topics: Set<TopicDto>,
    val traceArrows: Set<TraceArrow<GraphNodeDto, GraphNodeDto>>
    )
