package ru.vood.kafkatracer.service

import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.RequestCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import ru.vood.kafkatracer.request.meta.dto.*
import java.util.*

@Service
class UiRemapController(
    val requestCache: RequestCache
) {

    fun getJsGraph(requestGraphDto: RequestGraphDto): JsGraph {
        val (traceArrows, topicListeners) = requestCache.cache[requestGraphDto]
        val arrs = traceArrows.map { Arr(getNode(it.from), getNode(it.to)) }

        val topicsMsg = traceArrows
            .flatMap { listOf(it.from, it.to) }
            .filterIsInstance<TopicDto>()
            .associate { getNode(it) to topicListeners[it]?.lastKafkaMessage?.get() }


        val nodes = arrs.flatMap {
            listOf(it.from, it.to)
        }
            .distinct()
            .sortedBy { it.typeNode.name + it.name }
            .withIndex()
            .map { node ->
                val kafkaData = topicsMsg[node.value]
                val dateStr = kafkaData?.let { Date(it.timestamp).toString() }
                val id = kafkaData?.identity?.id
                val uid = kafkaData?.identity?.uuid
                UINode(node.index, node.value.name, node.value.typeNode, id, uid, dateStr)
            }

        val arrows = arrs.withIndex()
            .map { arrIdx ->
                val index = arrIdx.index
                val arr = arrIdx.value
                val fromIndex = nodes.find { n -> n.name == arr.from.name && n.typeNode == arr.from.typeNode }!!.index
                val toIndex = nodes.find { n -> n.name == arr.to.name && n.typeNode == arr.to.typeNode }!!.index
                UIArrows(index, fromIndex, toIndex)
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