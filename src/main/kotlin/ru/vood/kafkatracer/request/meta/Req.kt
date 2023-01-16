package ru.vood.kafkatracer.request.meta

import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl
import ru.vood.kafkatracer.request.meta.dto.GraphNodeDto
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TraceArrow


@Service
class Req(
    cfgServerUrl: ConfigurationServerUrl,
    restTemplate: RestTemplate
) : AbstractRestRequest(cfgServerUrl, restTemplate) {


    fun arrowsByTopic(groupId: String): Set<TraceArrow<GraphNodeDto, GraphNodeDto>> {

        val traceArrows = restTemplate.getForObject(
            fullUrl("tracking/arrows/$groupId"),
            String::class.java
        )
            ?.let { Json.decodeFromString(SetSerializer(JsonArrow.serializer()), it) }
            ?.map { it.arrow() }
            ?.toSet() ?: setOf()
        return traceArrows

    }

}