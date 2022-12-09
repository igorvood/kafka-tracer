package ru.vood.kafkatracer.request.meta.cache

import com.google.common.cache.CacheLoader
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.dto.ListenTopics
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvJson
import ru.vood.kafkatracer.request.meta.dto.TopicJson

@Service
class UserCacheLoader(val req: Req) : CacheLoader<String, ListenTopics>() {

    override fun load(key: String): ListenTopics {
        val arrowsByTopic = req.arrowsByTopic(key)
        val toSet = arrowsByTopic
            .map {
                 when (val to = it.to) {
                    is TopicJson -> to
                    is FlinkSrvJson -> {
                        val from = it.from
                        if (from is TopicJson) {
                            from
                        } else throw java.lang.IllegalStateException("wrong structure")
                    }
                }
            }.toSet()

        return ListenTopics(toSet, arrowsByTopic)
    }
}