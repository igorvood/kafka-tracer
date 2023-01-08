package ru.vood.kafkatracer.request.meta.cache.dto

import kotlinx.serialization.Serializable


@Serializable
data class Identity(
    val id: String? = null,
    val uuid: String? = null,
)