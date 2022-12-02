package ru.vood.kafkatracer.test

import com.google.common.cache.CacheLoader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import java.util.*

@Service
class Run: CommandLineRunner {
    override fun run(vararg args: String?) {
        object : CacheLoader<String?, String?>() {

            override fun load(key: String): String {
                return key.uppercase(Locale.getDefault())
            }
        }
    }
}