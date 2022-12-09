package ru.vood.kafkatracer.request.meta.dto

import ru.vood.kafkatracer.request.meta.dto.JsArrows
import ru.vood.kafkatracer.request.meta.dto.JsNode

data class JsGraph(val nodes: List<JsNode>,val  arrows: List<JsArrows>) {

}
