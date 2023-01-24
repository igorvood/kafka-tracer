package ru.vood.kafkatracer.request.meta.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.vood.kafkatracer.appProps.CacheProperty
import java.util.concurrent.TimeUnit

abstract class AbstractCacheBuilder<K, V>(cacheProperty: CacheProperty) {

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected abstract val removalListener: RemovalListener<K, V>
    protected abstract val loader: CacheLoader<K, V>

    val cache: LoadingCache<K, V> = CacheBuilder.newBuilder()
        .expireAfterAccess(cacheProperty.timeSafety, TimeUnit.MINUTES)
        .removalListener(removalListener)
        .build(loader)
}