package ru.vood.kafkatracer.request.meta.dto


@kotlinx.serialization.Serializable
data class JsonArrow(
    val from: GraphNodeDto,

    val to: GraphNodeDto,
) {


    @Deprecated("лишний класс TraceArrow")
    fun arrow(): TraceArrow<GraphNodeDto, GraphNodeDto> {

//        TraceArrow(from, to)
//        val from = fromSrv ?: fromTopic!!
//        val traceArrow = TraceArrow(fromSrv ?: fromTopic!!, toSrv ?: toTopic!!)

        return TraceArrow(from, to)

    }

//    companion object {
//        fun of(from: GraphNodeDto, to: GraphNodeDto): JsonArrow {
//            val pairFrom = when (from) {
//                is TopicDto -> null to from
//                is FlinkSrvDto -> from to null
//            }
//            val pairTo = when (to) {
//                is TopicDto -> null to to
//                is FlinkSrvDto -> to to null
//            }
//            return JsonArrow(pairFrom.first, pairFrom.second, pairTo.first, pairTo.second)
//        }
//    }
}