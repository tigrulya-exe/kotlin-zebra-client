package ru.nsu.manasyan.zebra.service

import ru.nsu.manasyan.zebra.client.ZebraDatabaseApiClient
import ru.nsu.manasyan.zebra.model.SearchRequest
import ru.nsu.manasyan.zebra.model.UpdateRecordRequest
import ru.nsu.manasyan.zebra.storage.TimestampStorage
import java.time.Instant
import java.util.function.Supplier

class ZebraRecordsTransferService(
    private val timestampStorage: TimestampStorage,
    private val dbApiClient: ZebraDatabaseApiClient,
    private val currentTimestampProvider: () -> Instant = Instant::now,
    private val sourceRecordsFilterProducer: (Instant) -> SearchRequest =
        { SearchRequest("@1=1011 @5=1 $it") }
) {
    suspend fun transferRecords(
        sourceDbIds: List<String>,
        targetDbId: String,
        defaultLastTransferredTimestamp: Instant = Instant.EPOCH
    ) = sourceDbIds.forEach {
        transferRecords(it, targetDbId, defaultLastTransferredTimestamp)
    }

    private suspend fun transferRecords(
        sourceDbId: String,
        targetDbId: String,
        defaultLastTransferredTimestamp: Instant = Instant.EPOCH
    ) {
        val dbLastTransferred = timestampStorage.getTimestamp(sourceDbId)
            ?: timestampStorage.putTimestamp(sourceDbId, defaultLastTransferredTimestamp)
        val currentTimestamp = currentTimestampProvider.invoke()

        val records = dbApiClient.search(
            sourceDbId, sourceRecordsFilterProducer.invoke(dbLastTransferred)
        ).data.data.records

        records.map { it.recordData }
            .forEach { dbApiClient.updateRecord(targetDbId, UpdateRecordRequest(it)) }

        timestampStorage.putTimestamp(sourceDbId, currentTimestamp)
    }
}