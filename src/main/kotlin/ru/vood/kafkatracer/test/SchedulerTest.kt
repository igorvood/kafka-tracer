package ru.vood.kafkatracer.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.TopicRequestGraphDto
import java.util.Date

@Service
class SchedulerTest(val userCache : UserCache) {
    private val logger: Logger = LoggerFactory.getLogger(SchedulerTest::class.java)
    @Scheduled(fixedDelay = 10000)
    fun sd(){
    val joinToString =
        userCache.userCache[TopicRequestGraphDto("dev_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto")]
            .messageKafka
            .map { it.key + " " + Date(it.value.timestamp) }
            .joinToString(", ")
                logger.info("userCache size "+userCache.userCache.size())
//        logger.info(joinToString)

    }

}