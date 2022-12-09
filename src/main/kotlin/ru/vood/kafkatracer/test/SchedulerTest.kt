package ru.vood.kafkatracer.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.vood.kafkatracer.request.meta.cache.UserCache
import ru.vood.kafkatracer.request.meta.cache.dto.RequestGraphDto
import java.util.Date

//@Service
class SchedulerTest(val userCache : UserCache) {
    private val logger: Logger = LoggerFactory.getLogger(SchedulerTest::class.java)
//    @Scheduled(fixedDelay = 10000)
    fun sd(){
    val topicWithLastDate =
        userCache.userCache[RequestGraphDto("dev_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto")]
            .messageKafka
            .map { it.key to Date(it.value.timestamp) }
            .toMap()


        val maxOfLengthNameTopic = topicWithLastDate.keys.maxOf { it.length }

        val maxOfLengthDate = topicWithLastDate.values.maxOf { it.toString().length }

        val tableStr = topicWithLastDate
            .map { tt ->

                val date = tt.value.toString()
                padRight(tt.key, maxOfLengthNameTopic - tt.key.length) to padRight(
                    date,
                    maxOfLengthDate - date.length
                )

            }.joinToString("\n") { "| ${it.first} | ${it.second} |" }



        val totalStrLen = maxOfLengthNameTopic + maxOfLengthDate + 7
        val repeat = "-".repeat(totalStrLen)

        val s = "\n" + repeat + "\n" + tableStr + "\n" + repeat


//            .joinToString(", ")
//                logger.info("userCache size "+userCache.userCache.size())
        logger.info(s)

    }

    fun padLeft(s: String, n: Int): String {
        return "%${n}s".format(s)
    }

    fun padRight(s: String, n: Int): String {
        return "%-${n}s".format(s)
    }

}