package ru.vood.kafkatracer.request.meta

import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl
import ru.vood.kafkatracer.request.meta.dto.JsonArrow


@Service
class ArrowsRepository(
    cfgServerUrl: ConfigurationServerUrl,
    restTemplate: RestTemplate
) : AbstractRestRequest(cfgServerUrl, restTemplate) {

    fun arrowsByTopic(groupId: String): Set<JsonArrow> {

        return sendRestGet1<String>(groupId)
            ?.let { Json.decodeFromString(SetSerializer(JsonArrow.serializer()), it) }
            ?.toSet() ?: setOf()
    }

}