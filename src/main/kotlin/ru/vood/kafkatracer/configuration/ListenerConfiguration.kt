package ru.vood.kafkatracer.configuration

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.MessageListener
import ru.vood.kafkatracer.appProps.ListenGraph
import ru.vood.kafkatracer.appProps.ListenTopics
import ru.vood.kafkatracer.request.meta.Req
import ru.vood.kafkatracer.request.meta.dto.FlinkSrvJson
import ru.vood.kafkatracer.request.meta.dto.TopicJson


@Configuration
class ListenerConfiguration {

    @Bean
    fun getListenGraph(req: Req): ListenGraph {
        return ListenGraph(req.arrowsByTopic("dev_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto"))
    }

    @Bean
    fun getListenTopic(listenGraph: ListenGraph): ListenTopics {
        val map = listenGraph.arrows
            .map {
                val graphNodeJson = when (val to = it.to) {
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
class MyMessageListener : MessageListener<String, String>
{
    override fun onMessage(data: ConsumerRecord<String, String>) {
        TODO("Not yet implemented")
    }
}