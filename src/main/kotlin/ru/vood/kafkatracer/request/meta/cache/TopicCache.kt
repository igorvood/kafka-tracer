package ru.vood.kafkatracer.request.meta.cache

import arrow.core.continuations.AtomicRef
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalListener
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TopicCache(
    val cnsFactory: ConsumerFactory<String, String>,
) : AbstractCacheBuilder<TopicDto, TopicCacheValue>() {

    private val processKafkaMessage: (String, ConcurrentHashMap<String, KafkaData>) -> ((KafkaData) -> Unit) =
        { topicName, messageKafkaMap ->
            val process: (KafkaData) -> Unit = { km ->
                val prev = messageKafkaMap.put(topicName, km)
                logger.info("""last msg ${Date(km.timestamp)} topic ${topicName}, prev msg ${prev?.timestamp}""")
            }
            process
        }

    override val removalListener: RemovalListener<TopicDto, TopicCacheValue>
        get() = RemovalListener<TopicDto, TopicCacheValue> { entity ->
            entity.value?.listener?.let {
                logger.info("=======stop consumer for topic ${entity.key?.name ?: "Unknown"} $it beanName ${it.beanName}  cause ${entity.cause}")
                it.stop()
            }
        }

    @Lookup
    fun messageListenerContainer(
        topic: String,
        messageApplyFun: (KafkaData) -> Unit,
        cnsFactory: () -> ConsumerFactory<String, String>
    ): AbstractMessageListenerContainer<*, *> {
        error("must be implemented by Spring")
    }

    override val loader: CacheLoader<TopicDto, TopicCacheValue>
        get() = object : CacheLoader<TopicDto, TopicCacheValue>() {
            override fun load(topic: TopicDto): TopicCacheValue {
                val messageKafka = processKafkaMessage(topic.name, ConcurrentHashMap<String, KafkaData>())
                    .let { messageListenerContainer(topic.name, it) { cnsFactory } }

                return TopicCacheValue(messageKafka)
            }
        }

}

data class TopicCacheValue(
    val listener: AbstractMessageListenerContainer<*, *>,
    val lastKafkaMessage: AtomicRef<KafkaData?> = AtomicRef(null),

    )