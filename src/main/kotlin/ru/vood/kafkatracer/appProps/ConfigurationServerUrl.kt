package ru.vood.kafkatracer.appProps

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "configuration.server")
data class ConfigurationServerUrl(
    val host: String,
    val port: Int,
)
