package ru.vood.kafkatracer.request.meta.cache

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.MessageListener
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData
import java.util.*

class KafkaMessageListener(
    private val topic: String,
    private val messageKafka: MutableMap<String, KafkaData>
) : MessageListener<String, String> {
    private val logger: Logger = LoggerFactory.getLogger(KafkaMessageListener::class.java)

    override fun onMessage(data: ConsumerRecord<String, String>) {
        val key = data.key()
        val headers = data.headers().toArray()
        val timestamp = data.timestamp()
        val value = data.value()
        val pip = data.topic()

        val kafkaData = KafkaData(key, headers, timestamp, value, pip)

        logger.info("""$topic last msg ${Date(timestamp)}""")

        messageKafka[topic] = kafkaData
    }
}