package ru.vood.kafkatracer.request.meta.dto

data class UINode(
    val index: Int,
    val name: String,
    val typeNode: TypeNodeEnum,
    val id: String?,
    val uid: String?,
    val time: String?,
)
