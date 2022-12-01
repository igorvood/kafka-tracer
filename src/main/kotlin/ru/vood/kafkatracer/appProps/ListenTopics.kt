package ru.vood.kafkatracer.appProps

import ru.vood.kafkatracer.request.meta.dto.TopicJson

data class ListenTopics(val topics: Set<TopicJson>)
