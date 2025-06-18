package com.n7.localmind.core.cache

import android.content.Context
import kotlinx.serialization.KSerializer

class CacheObjectProviderFactory(
    private val context: Context,
) {

    fun <T> getCacheObject(key: String, serializer: KSerializer<T>, defaultValue: T): CacheObject<T> {
        return CacheObjectImpl(context, key, serializer, defaultValue)
    }
} 