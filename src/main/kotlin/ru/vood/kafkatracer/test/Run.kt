package ru.vood.kafkatracer.test

import org.springframework.boot.CommandLineRunner
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto

//@Service
class Run(val userCache: UserCache) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val get =
            userCache.cache.get(RequestGraphDto("dev_bevents__realtime__case_71__uaspdto_dlq"))
        println(get)
    }
}