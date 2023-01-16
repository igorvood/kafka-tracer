package ru.vood.kafkatracer.request.meta

import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl

abstract class AbstractRestRequest(
    val cfgServerUrl: ConfigurationServerUrl,
    val restTemplate: RestTemplate
) {
    fun fullUrl(restEnd: String): String = "http://${cfgServerUrl.host}:${cfgServerUrl.port}/$restEnd"


    protected inline fun <reified T> sendRestGet1(groupId: String): T? {

        return restTemplate.getForObject(
            fullUrl("tracking/arrows/$groupId"),
            T::class.java
        )

    }


}