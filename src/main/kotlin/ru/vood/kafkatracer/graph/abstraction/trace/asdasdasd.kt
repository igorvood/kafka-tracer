package ru.vood.kafkatracer.graph.abstraction.trace

fun main() {

    val topic1 = Topic("asd", setOf())
    val topic2 = Topic("asd", setOf(topic1))
    topic2
}