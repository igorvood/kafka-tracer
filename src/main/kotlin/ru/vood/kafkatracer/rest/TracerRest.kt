package ru.vood.kafkatracer.rest

import com.google.common.cache.LoadingCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.cache.dto.UserRequestListen
import ru.vood.kafkatracer.request.meta.dto.*
import java.util.*

@RestController
@CrossOrigin
class TracerRest(
    val userCache: UserCache
) {


    private val logger: Logger = LoggerFactory.getLogger(TracerRest::class.java)

    //    @Operation(summary = "Получить связи для трекинга", tags = ["Связи"])
    @GetMapping("/arrows/byGroup/{groupId}")
    fun arrowsByGroup(@PathVariable groupId: String): JsGraph {

        val cache = userCache.cache
        return extractJsGraphFromChache(cache, groupId)

    }

    private fun extractJsGraphFromChache(
        cache: LoadingCache<RequestGraphDto, UserRequestListen>,
        groupId: String
    ): JsGraph {
        val userRequestListen = cache[RequestGraphDto(groupId)]
        logger.info("========================" + cache.asMap().keys + "===============================" + userRequestListen.messageKafka)
        val traceArrows = userRequestListen.listenTopics.traceArrows
        val messageKafka = userRequestListen.messageKafka

        val arrs = traceArrows.map { Arr(getNode(it.from), getNode(it.to)) }

        val nodes = arrs.flatMap {
            listOf(it.from, it.to)
        }
            .distinct()
            .sortedBy { it.typeNode.name + it.name }
            .withIndex()
            .map { node ->
                val kafkaData = messageKafka[node.value.name]
                val dateStr = kafkaData?.let { Date(it.timestamp).toString() }
                val id = kafkaData?.identity?.id
                val uid = kafkaData?.identity?.uuid
                JsNode(node.index, node.value.name, node.value.typeNode, id, uid, dateStr)
            }

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

    private fun getNode(from: GraphNodeDto): Node {
        return when (from) {
            is TopicDto -> Node(from.fullName, TypeNodeEnum.TOPIC)
            is FlinkSrvDto -> Node(from.fullName, TypeNodeEnum.FLINK)
        }
    }

    private data class Node(val name: String, val typeNode: TypeNodeEnum)

    private data class Arr(val from: Node, val to: Node)


}