package ru.nsu.manasyan.zebra.storage

import java.time.Instant

interface TimestampStorage {
    suspend fun getTimestamp(dbId: String): Instant?

    suspend fun putTimestamp(dbId: String, timestamp: Instant): Instant
}