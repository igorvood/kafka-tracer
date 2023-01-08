package ru.vood.kafkatracer.request.meta

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl
import ru.vood.kafkatracer.request.meta.dto.GraphNodeJson
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TraceArrow


@Service
class Req(cfgServerUrl: ConfigurationServerUrl,
          restTemplate: RestTemplate
) : AbstractRestRequest(cfgServerUrl, restTemplate) {


    fun arrowsByTopic(groupId: String): Set<TraceArrow<GraphNodeJson, GraphNodeJson>> {


        val forObject = restTemplate.getForObject(
            fullUrl("arrows/byGroup/$groupId"),
            Array<JsonArrow>::class.java
        )
        return forObject!!
            .map { it.arrow() }
            .toSet()


    }

}