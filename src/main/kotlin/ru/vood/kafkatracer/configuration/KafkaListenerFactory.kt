package ru.vood.kafkatracer.configuration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.KafkaMessageListener
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData

@Service
class KafkaListenerFactory(private val kafkaProperties: KafkaProperties) {

    fun messageListenerContainer(topic: String, messageKafka: MutableMap<String, KafkaData>): KafkaMessageListenerContainer<String, String> {
        val containerProperties = ContainerProperties(topic)
        containerProperties.messageListener = KafkaMessageListener(topic, messageKafka)
        val consumerFactory: ConsumerFactory<String, String> = DefaultKafkaConsumerFactory(consumerProperties())
        val listenerContainer = KafkaMessageListenerContainer(consumerFactory, containerProperties)
        listenerContainer.isAutoStartup = false

        // bean name is the prefix of kafka consumer thread name
        listenerContainer.setBeanName("kafka-message-listener")

        listenerContainer.start()

        return listenerContainer
    }


    private fun consumerProperties(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers.joinToString(",")
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test"
        return props
    }
}