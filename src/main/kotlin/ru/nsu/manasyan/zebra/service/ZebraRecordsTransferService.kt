package ru.nsu.manasyan.zebra.service

import ru.nsu.manasyan.zebra.client.ZebraDatabaseApiClient
import ru.nsu.manasyan.zebra.model.SearchRequest
import ru.nsu.manasyan.zebra.model.UpdateRecordRequest
import ru.nsu.manasyan.zebra.storage.TimestampStorage
import java.time.Instant

class ZebraRecordsTransferService(
    private val timestampStorage: TimestampStorage,
    private val dbApiClient: ZebraDatabaseApiClient
) {
    suspend fun transferRecords(
        sourceDbIds: List<String>,
        targetDbId: String
    ) = sourceDbIds.forEach { transferRecords(it, targetDbId) }

    private suspend fun transferRecords(sourceDbId: String, targetDbId: String) {
        val dbLastTransferred = timestampStorage.getTimestamp(sourceDbId)
            ?: timestampStorage.putTimestamp(sourceDbId, Instant.EPOCH)
        val currentTimestamp = Instant.now()

        val records = dbApiClient.search(
            sourceDbId, SearchRequest("@1=1011 @5=1 $dbLastTransferred")
        ).data.data.records

        records.map { it.recordData }
            .forEach { dbApiClient.updateRecord(targetDbId, UpdateRecordRequest(it)) }

        timestampStorage.putTimestamp(sourceDbId, currentTimestamp)
    }
}