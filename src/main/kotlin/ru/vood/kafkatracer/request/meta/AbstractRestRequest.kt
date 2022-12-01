package ru.vood.kafkatracer.request.meta

import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl

abstract class AbstractRestRequest(
    val restTemplate: RestTemplate
    ) {

    lateinit var cfgServerUrl: ConfigurationServerUrl



}