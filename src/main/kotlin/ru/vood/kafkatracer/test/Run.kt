package ru.vood.kafkatracer.test

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.Req

@Service
class Run(val req: Req): CommandLineRunner {
    override fun run(vararg args: String?) {
        println(req.arrowsByTopic())
    }
}