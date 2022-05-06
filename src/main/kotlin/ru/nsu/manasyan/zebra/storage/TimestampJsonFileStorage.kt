package ru.nsu.manasyan.zebra.storage

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nsu.manasyan.zebra.util.fromJson
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class TimestampJsonFileStorage(
    private val filePath: Path = Path.of("./TIMESTAMPS.json"),
    private val jsonSerializer: Gson
) : TimestampStorage {

    private val lock = ReentrantReadWriteLock()
    private val timestampMap = initTimestampMap()

    override suspend fun getTimestamp(dbId: String): Instant? = lock.read { timestampMap[dbId] }

    override suspend fun putTimestamp(
        dbId: String,
        timestamp: Instant
    ): Instant = withContext(Dispatchers.IO) {
        lock.write {
            timestampMap[dbId] = timestamp
            val jsonMap = lock.read { jsonSerializer.toJson(timestampMap) }
            filePath.writeText(jsonMap)
        }
        timestamp
    }

    private fun initTimestampMap(): MutableMap<String, Instant> =
        if (filePath.exists()) {
            lock.write {
                jsonSerializer.fromJson(filePath.readText())
            }
        } else mutableMapOf()
}