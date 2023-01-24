package ru.vood.kafkatracer.request.meta.cache

import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalListener
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.appProps.CacheProperty
import ru.vood.kafkatracer.request.meta.ArrowsRepository
import ru.vood.kafkatracer.request.meta.cache.dto.GroupRequestListen
import ru.vood.kafkatracer.request.meta.cache.dto.ListenTopics
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvDto
import ru.vood.kafkatracer.request.meta.dto.TopicDto

@Service
class RequestCache(
    private val req: ArrowsRepository,
    private val topicCache: TopicCache,
    cacheProperty: CacheProperty
) : AbstractCacheBuilder<RequestGraphDto, GroupRequestListen>(cacheProperty) {

    override val removalListener: RemovalListener<RequestGraphDto, GroupRequestListen>
        get() = RemovalListener<RequestGraphDto, GroupRequestListen> { entity ->
            logger.info("=======delete group cache ${entity.key}")
        }

    override val loader: CacheLoader<RequestGraphDto, GroupRequestListen>
        get() = object : CacheLoader<RequestGraphDto, GroupRequestListen>() {
            override fun load(key: RequestGraphDto): GroupRequestListen {
                val (topics, traceArrows) = requestGraph(key)

                val toMap = topics.associateWith { topicCache.cache.get(it) }
                return GroupRequestListen(traceArrows, toMap)
            }
        }

    protected fun requestGraph(requestGraphDto: RequestGraphDto): ListenTopics {

        val traceArrows = req.arrowsByTopic(requestGraphDto.groupId)

        val topics = traceArrows.map {
            val graphNodeJson = when (val to = it.to) {
                is TopicDto -> to
                is FlinkSrvDto -> {
                    val from = it.from
                    if (from is TopicDto) {
                        from
                    } else throw java.lang.IllegalStateException("zsda")
                }

            }
            graphNodeJson
        }.toSet()

        return ListenTopics(topics, traceArrows)
    }
}