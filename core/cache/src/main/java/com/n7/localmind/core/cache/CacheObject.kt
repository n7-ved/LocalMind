package com.n7.localmind.core.cache

import kotlinx.coroutines.flow.Flow

interface CacheObject<T> {
    suspend fun put(value: T)
    suspend fun get(): T
    fun observe(): Flow<T>
} 