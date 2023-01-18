package ru.vood.kafkatracer.request.meta.dto

@kotlinx.serialization.Serializable
data class GroupServiceDto (val id: String,
                            val description: String,
)