package ru.vood.kafkatracer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class KafkaTracerApplication

fun main(args: Array<String>) {
    runApplication<KafkaTracerApplication>(*args)
}
