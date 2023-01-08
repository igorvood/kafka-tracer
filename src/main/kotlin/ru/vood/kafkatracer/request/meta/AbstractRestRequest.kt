package ru.vood.kafkatracer.request.meta

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl

abstract class AbstractRestRequest(
    restTemplateBuilder: RestTemplateBuilder
) {

    private lateinit var cfgServerUrl: ConfigurationServerUrl

    protected val restTemplate: RestTemplate = restTemplateBuilder.build()

    @Autowired
    private fun set(cfgServerUrl: ConfigurationServerUrl) {
        this.cfgServerUrl = cfgServerUrl
    }

    fun fullUrl(restEnd: String): String = "http://${cfgServerUrl.host}:${cfgServerUrl.port}/$restEnd"


}