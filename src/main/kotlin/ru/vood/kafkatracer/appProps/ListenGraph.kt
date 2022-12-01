package ru.vood.kafkatracer.appProps

import ru.vood.kafkatracer.request.meta.dto.GraphNodeJson
import ru.vood.kafkatracer.request.meta.dto.TraceArrow

data class ListenGraph (val arrows: Set<TraceArrow<GraphNodeJson,GraphNodeJson >>)