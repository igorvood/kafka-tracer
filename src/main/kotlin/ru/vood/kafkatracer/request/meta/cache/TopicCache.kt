package ru.vood.kafkatracer.request.meta.cache

import arrow.core.continuations.AtomicRef
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalListener
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.appProps.CacheProperty
import ru.vood.kafkatracer.request.meta.cache.dto.KafkaData
import ru.vood.kafkatracer.request.meta.cache.dto.TopicCacheValue
import ru.vood.kafkatracer.request.meta.dto.TopicDto
import java.util.*

@Service
class TopicCache(
    val cnsFactory: ConsumerFactory<String, String>,
    cacheProperty: CacheProperty
) : AbstractCacheBuilder<TopicDto, TopicCacheValue>(cacheProperty) {

    private val processKafkaMessage: (String, AtomicRef<KafkaData?>) -> ((KafkaData) -> Unit) =
        { topicName, messageKafkaMap ->
            val process: (KafkaData) -> Unit = { km ->
                val get = messageKafkaMap.get()
                logger.info("""last msg ${Date(km.timestamp)} topic ${topicName}, prev msg ${get?.timestamp}""")
                messageKafkaMap.set(km)
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
                val atomicRef = AtomicRef<KafkaData?>(null)
                val messageKafka = processKafkaMessage(topic.name, atomicRef)
                    .let { saveMessageFunction ->
                        messageListenerContainer(
                            topic.name,
                            saveMessageFunction
                        ) { cnsFactory }
                    }

                return TopicCacheValue(messageKafka, atomicRef)
            }
        }

}
