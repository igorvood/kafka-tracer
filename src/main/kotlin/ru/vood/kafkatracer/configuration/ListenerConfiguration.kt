package ru.vood.kafkatracer.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.vood.kafkatracer.appProps.ListenGraph
import ru.vood.kafkatracer.appProps.ListenTopics
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvJson
import ru.vood.kafkatracer.request.meta.dto.TopicJson

@Configuration
class ListenerConfiguration {

    @Bean
    fun getListenGraph(req: Req): ListenGraph {
        return ListenGraph(req.arrowsByTopic())
    }

    @Bean
    fun getListenTopic(listenGraph: ListenGraph): ListenTopics {
        val map = listenGraph.arrows
            .map {
                val to = it.to
                val graphNodeJson = when (to) {
                    is TopicJson -> to
                    is FlinkSrvJson -> {
                        val from = it.from
                        if (from is TopicJson) {
                            from
                        } else throw java.lang.IllegalStateException("zsda")
                    }

                }
                graphNodeJson
            }.toSet()
        return ListenTopics(map)
    }
}