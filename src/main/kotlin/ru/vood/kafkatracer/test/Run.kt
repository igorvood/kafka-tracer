package ru.vood.kafkatracer.test

import com.google.common.cache.CacheLoader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import java.util.*

//@Service
class Run(val userCache : UserCache) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val get =
            userCache.userCache.get(RequestGraphDto("dev_bevents__realtime__case_71__uaspdto_dlq"))
        println(get)
    }
}