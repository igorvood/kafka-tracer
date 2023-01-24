package ru.vood.kafkatracer.appProps

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "configuration.server.cache")
data class CacheProperty (val timeSafety: Long)
