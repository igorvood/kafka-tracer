package ru.vood.kafkatracer.request.meta.dto

sealed interface GraphNodeJson {
    val fullName: String
}

data class TopicJson(
    val name: String,
) : GraphNodeJson {
    override val fullName: String
        get() = name
}

data class FlinkSrvJson(
    val name: String,
    val profileId: String
) : GraphNodeJson {
    override val fullName: String
        get() = name + "_" + profileId
}