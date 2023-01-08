package ru.vood.kafkatracer.request.meta.cache.dto

import ru.vood.kafkatracer.request.meta.dto.GraphNodeJson
import ru.vood.kafkatracer.request.meta.dto.TopicJson
import ru.vood.kafkatracer.request.meta.dto.TraceArrow

data class ListenTopics(
    val topics: Set<TopicJson>,
    val traceArrows: Set<TraceArrow<GraphNodeJson, GraphNodeJson>>
    )
