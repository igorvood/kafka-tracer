package ru.vood.kafkatracer.request.meta

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.dto.GraphNodeJson
import ru.vood.kafkatracer.request.meta.dto.JsonArrow
import ru.vood.kafkatracer.request.meta.dto.TraceArrow


@Service
class Req(restTemplateBuilder: RestTemplateBuilder) : AbstractRestRequest(restTemplateBuilder) {


    fun arrowsByTopic(groupId: String): Set<TraceArrow<GraphNodeJson, GraphNodeJson>> {


        return restTemplate.getForObject(
            fullUrl("arrows/byGroup/$groupId"),
            Array<JsonArrow>::class.java
        )!!
            .map { it.arrow() }
            .toSet()


    }

}