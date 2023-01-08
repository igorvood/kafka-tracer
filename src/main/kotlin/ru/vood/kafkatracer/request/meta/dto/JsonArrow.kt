package ru.vood.kafkatracer.request.meta.dto

data class JsonArrow(
    val fromSrv: FlinkSrvJson?,
    val fromTopic: TopicJson?,

    val toSrv: FlinkSrvJson?,
    val toTopic: TopicJson?,

    ) {


    fun arrow(): TraceArrow<GraphNodeJson, GraphNodeJson> {
        val from = fromSrv ?: fromTopic!!
        val traceArrow = TraceArrow(fromSrv ?: fromTopic!!, toSrv ?: toTopic!!)

        return traceArrow

    }

    companion object {
        fun of(from: GraphNodeJson, to: GraphNodeJson): JsonArrow {
            val pairFrom = when (from) {
                is TopicJson -> null to from
                is FlinkSrvJson -> from to null
            }
            val pairTo = when (to) {
                is TopicJson -> null to to
                is FlinkSrvJson -> to to null
            }
            return JsonArrow(pairFrom.first, pairFrom.second, pairTo.first, pairTo.second)
        }
    }
}