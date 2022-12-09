package ru.vood.kafkatracer.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.dto.*

@RestController
@CrossOrigin
class TracerRest(
    val userCache: UserCache
) {

    //    @Operation(summary = "Получить связи для трекинга", tags = ["Связи"])
    @GetMapping("/arrows/byGroup/{groupId}")
    fun arrowsByGroup(@PathVariable groupId: String): JsGraph {

        val userRequestListen = userCache.userCache[RequestGraphDto(groupId)]
        val traceArrows = userRequestListen.listenTopics.traceArrows


        val arrs = traceArrows.map { Arr(getNode(it.from), getNode(it.to)) }

        val nodes = arrs.flatMap {
            listOf(it.from, it.to)
        }
            .distinct()
            .sortedBy { it.typeNode.name + it.name }
            .withIndex()
            .map { JsNode(it.index, it.value.name, it.value.typeNode) }

        val arrows = arrs.withIndex()
            .map { arrIdx ->
                val index = arrIdx.index
                val arr = arrIdx.value
                val fromIndex = nodes.find { n -> n.name == arr.from.name && n.typeNode == arr.from.typeNode }!!.index
                val toIndex = nodes.find { n -> n.name == arr.to.name && n.typeNode == arr.to.typeNode }!!.index
                JsArrows(index, fromIndex, toIndex)
            }

        return JsGraph(nodes, arrows)

    }

    private fun getNode(from: GraphNodeJson): Node {
        return when (from) {
            is TopicJson -> Node(from.fullName, TypeNodeEnum.TOPIC)
            is FlinkSrvJson -> Node(from.fullName, TypeNodeEnum.FLINK)
        }
    }

    private data class Node(val name: String, val typeNode: TypeNodeEnum)

    private data class Arr(val from: Node, val to: Node)


}