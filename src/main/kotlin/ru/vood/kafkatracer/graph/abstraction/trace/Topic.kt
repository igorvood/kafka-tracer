package ru.vood.kafkatracer.graph.abstraction.trace

import ru.vood.kafkatracer.graph.abstraction.AbstractNode
import ru.vood.kafkatracer.graph.abstraction.INode
import kotlin.reflect.KClass

class Topic(
    val name: String,
    val nextNodes: Set<INode>,
) : AbstractNode() {
    override fun nextNodes(): Set<INode> {
        return nextNodes
    }

    override fun checkLimitationClassNextNodes(): Set<KClass<out INode>> {
        return setOf(FlinkService::class)
    }

    init {
        val badNodesNodes = getBadNodesNodes()
        require(badNodesNodes.isEmpty()) { "end nodes do not fit the restrictions $badNodesNodes" }
    }
}
