package ru.vood.kafkatracer.request.meta.cache.dto

import arrow.core.continuations.AtomicRef
import org.springframework.kafka.listener.AbstractMessageListenerContainer

data class TopicCacheValue(
    val listener: AbstractMessageListenerContainer<*, *>,
    val lastKafkaMessage: AtomicRef<KafkaData?>,

    )