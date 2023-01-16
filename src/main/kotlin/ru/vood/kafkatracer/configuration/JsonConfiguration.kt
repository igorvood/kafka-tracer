package ru.vood.kafkatracer.configuration

import kotlinx.serialization.json.Json

val customJson = Json {
    ignoreUnknownKeys = true
}