package ru.vood.kafkatracer.request.meta.dto


@kotlinx.serialization.Serializable
data class JsonArrow(
    val from: GraphNodeDto,

    val to: GraphNodeDto,
)