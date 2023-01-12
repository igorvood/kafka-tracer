package ru.vood.kafkatracer.request.meta.dto

data class TraceArrow<out F : GraphNodeDto, out T : GraphNodeDto>(val from: F, val to: T) {

}
