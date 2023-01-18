package ru.vood.kafkatracer.request.meta

import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.vood.kafkatracer.appProps.ConfigurationServerUrl
import ru.vood.kafkatracer.request.meta.dto.GroupServiceDto
import ru.vood.kafkatracer.request.meta.dto.JsonArrow

@Service
class GroupRepository(
    cfgServerUrl: ConfigurationServerUrl,
    restTemplate: RestTemplate
) : AbstractRestRequest(cfgServerUrl, restTemplate) {

    fun trackingGroup(): Set<GroupServiceDto> {

        return sendRestGet<String>("tracking/group")
            ?.let { Json.decodeFromString(SetSerializer(GroupServiceDto.serializer()), it) }
            ?.toSet() ?: setOf()
    }
}