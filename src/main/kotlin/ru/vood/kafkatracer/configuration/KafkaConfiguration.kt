package ru.vood.kafkatracer.configuration

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import ru.vood.kafkatracer.request.meta.cache.KafkaMessageListener
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData

@Configuration
class KafkaConfiguration {

    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, String> {
        val buildConsumerProperties = kafkaProperties.buildConsumerProperties()
        return DefaultKafkaConsumerFactory(buildConsumerProperties)
    }

    @Bean
    @Scope("prototype")
    fun kafkaListenerFactory1(
        topic: String,
        messageApplyFun: (KafkaData) -> Unit,
        cnsFactory: () -> ConsumerFactory<String, String>
    ): AbstractMessageListenerContainer<String, String> {
        val containerProperties = ContainerProperties(topic)
        containerProperties.messageListener = KafkaMessageListener(messageApplyFun)
        val listenerContainer: ConcurrentMessageListenerContainer<String, String> =
            ConcurrentMessageListenerContainer(cnsFactory(), containerProperties)
        listenerContainer.isAutoStartup = false

        // bean name is the prefix of kafka consumer thread name
        listenerContainer.setBeanName("kafka-message-listener-$topic")

        listenerContainer.start()

        return listenerContainer
    }
}