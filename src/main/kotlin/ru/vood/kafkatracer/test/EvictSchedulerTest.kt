package ru.vood.kafkatracer.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import ru.vood.kafkatracer.request.meta.cache.UserCache

//@Service
class EvictSchedulerTest(val userCache: UserCache) {
    private val logger: Logger = LoggerFactory.getLogger(EvictSchedulerTest::class.java)

    @Scheduled(fixedDelay = 20000)
    fun sd() {
        val joinToString =
            userCache
                .cache
                .asMap()
                .flatMap { it.value.topicListeners.values }
                .forEach { it.stop() }

        userCache
            .cache.invalidateAll()

    }

}