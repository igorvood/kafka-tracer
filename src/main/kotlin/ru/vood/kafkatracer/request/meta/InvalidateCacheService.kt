package ru.vood.kafkatracer.request.meta

import org.springframework.stereotype.Component
import ru.vood.kafkatracer.request.meta.cache.AbstractCacheBuilder
import ru.vood.kafkatracer.request.meta.cache.dto.GroupRequestListen
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.cache.dto.TopicCacheValue
import ru.vood.kafkatracer.request.meta.dto.TopicDto

@Component
class InvalidateCacheService(
    val requestCache: AbstractCacheBuilder<RequestGraphDto, GroupRequestListen>,
    val topicCache: AbstractCacheBuilder<TopicDto, TopicCacheValue>,
) {

    fun invalidateGroup(requestGraphDto: RequestGraphDto) {
        val groupRequestListen = requestCache.cache[requestGraphDto]

        groupRequestListen.topicListeners.keys.forEach { topicCache.cache.invalidate(it) }
        requestCache.cache.invalidate(requestGraphDto)
    }
}