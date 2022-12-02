package ru.vood.kafkatracer.request.meta.cache.dto

import ru.vood.kafkatracer.request.meta.dto.TopicJson

data class ListenTopics(val topics: Set<TopicJson>)
