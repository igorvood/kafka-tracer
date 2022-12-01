package ru.vood.kafkatracer.request.meta.dto

data class TraceArrow<out F : GraphNodeJson,out T : GraphNodeJson>(val from: F, val to: T)  {

}
