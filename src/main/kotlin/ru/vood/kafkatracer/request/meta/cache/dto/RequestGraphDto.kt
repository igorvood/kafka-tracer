package ru.vood.kafkatracer.request.meta.cache.dto

sealed interface RequestGraphDto {
}

data class TopicRequestGraphDto(
    val name: String,
) : RequestGraphDto

data class FlinkSrvRequestGraphDto(
    val name: String,
    val profileId: String
) : RequestGraphDto