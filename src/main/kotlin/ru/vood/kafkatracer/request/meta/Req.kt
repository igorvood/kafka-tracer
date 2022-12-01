package ru.vood.kafkatracer.request.meta

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.dto.JsonArrow


@Service
class Req(restTemplateBuilder: RestTemplateBuilder):AbstractRestRequest(restTemplateBuilder) {


    fun arrowsByTopic(){
        val message = restTemplate.getForObject(
            fullUrl("arrows/byTopic/dev_ivr__uasp_realtime__input_converter__mdm_cross_link__uaspdto"),
            Array<JsonArrow>::class.java
        )!!.toSet()
        println(message)


    }

}