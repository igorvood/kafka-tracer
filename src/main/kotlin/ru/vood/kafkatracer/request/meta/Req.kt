package ru.vood.kafkatracer.request.meta

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.dto.GraphNodeJson
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TraceArrow


@Service
class Req(restTemplateBuilder: RestTemplateBuilder):AbstractRestRequest(restTemplateBuilder) {


    fun arrowsByTopic(): Set<TraceArrow<GraphNodeJson, GraphNodeJson>> {
        val message: Set<TraceArrow<GraphNodeJson, GraphNodeJson>> = restTemplate.getForObject(
            fullUrl("arrows/byTopic/dev_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto"),
            Array<JsonArrow>::class.java
        )!!
            .map { it.arrow() }
            .toSet()


        return message


    }

}