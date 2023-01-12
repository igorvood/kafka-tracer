package ru.vood.kafkatracer.request.meta.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
sealed interface GraphNodeDto {
    @kotlinx.serialization.Transient
    val fullName: String
}

@kotlinx.serialization.Serializable
@SerialName("TopicJson")
data class TopicDto(
    val name: String,
) : GraphNodeDto {
    override val fullName: String
        get() = name
}

@kotlinx.serialization.Serializable
@SerialName("FlinkSrvJson")
data class FlinkSrvDto(
    val name: String,
    val profileId: String
) : GraphNodeDto {
    override val fullName: String
        get() = name + "_" + profileId
}