package ru.vood.kafkatracer.rest

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.vood.kafkatracer.request.meta.GroupRepository
import ru.vood.kafkatracer.request.meta.InvalidateCacheService
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.dto.*
import ru.vood.kafkatracer.service.UiRemapController
import java.util.*

@RestController
@CrossOrigin
class TracerRest(
    val uiRemapController: UiRemapController,
    val groupRepository: GroupRepository,
    val invalidateCacheService: InvalidateCacheService
) {


    private val logger: Logger = LoggerFactory.getLogger(TracerRest::class.java)

    //    @Operation(summary = "Получить связи для трекинга", tags = ["Связи"])
    @GetMapping("/arrows/byGroup/{groupId}")
    fun arrowsByGroup(@PathVariable groupId: String): JsGraph {
        return uiRemapController.getJsGraph(RequestGraphDto(groupId))
    }

    @GetMapping("/group/like")
    fun groupLike( groupIdLike: String, limit:Int): Set<GroupServiceDto> {
        return groupRepository.trackingGroup()
            .filter { it.id.contains(groupIdLike) }
            .take(limit)
            .toSet()
    }

    @GetMapping("/invalidate/{groupId}")
    fun invalidateGroup( @PathVariable groupId: String) {
        invalidateCacheService.invalidateGroup(RequestGraphDto(groupId))
    }


}