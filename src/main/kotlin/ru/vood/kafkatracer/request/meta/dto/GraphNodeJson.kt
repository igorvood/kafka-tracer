package ru.vood.kafkatracer.request.meta.dto

sealed interface GraphNodeJson

data class TopicJson(
    val name: String,
) : GraphNodeJson

data class FlinkSrvJson(
    val name: String,
    val profileId: String
) : GraphNodeJson