package ru.nsu.manasyan.zebra.service

import ru.nsu.manasyan.zebra.client.ZebraDatabaseApiClient
import ru.nsu.manasyan.zebra.model.ScanRequest
import ru.nsu.manasyan.zebra.model.SearchRequest
import ru.nsu.manasyan.zebra.model.UpdateRecordRequest
import ru.nsu.manasyan.zebra.storage.TimestampStorage
import ru.nsu.manasyan.zebra.util.formatInstant
import java.time.Instant
import java.util.function.Supplier

class ZebraRecordsTransferService(
    private val timestampStorage: TimestampStorage,
    private val dbApiClient: ZebraDatabaseApiClient,
    private val currentTimestampProvider: Supplier<Instant> = Supplier(Instant::now)
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
        val dbLastTransferred = formatInstant(
            timestampStorage.getTimestamp(sourceDbId)
                ?: timestampStorage.putTimestamp(sourceDbId, defaultLastTransferredTimestamp)
        )

        val currentTimestamp = currentTimestampProvider.get()

        val createdAfterTransfer = getNotTransferredRecordTimestamps(
            sourceDbId,
            "@1=1011 @6=3 $dbLastTransferred"
        )

        val modifiedAfterTransfer = getNotTransferredRecordTimestamps(
            sourceDbId,
            "@1=1012 @6=3 $dbLastTransferred"
        )

        val recordsToTransfer = dbApiClient.search(
            sourceDbId,
            buildNotTransferredRecordsSearchRequest(
                createdAfterTransfer,
                modifiedAfterTransfer
            )
        ).data.data.records

        recordsToTransfer?.map { it.recordData }
            ?.forEach { dbApiClient.updateRecord(targetDbId, UpdateRecordRequest(it)) }

        timestampStorage.putTimestamp(sourceDbId, currentTimestamp)
    }

    private fun buildNotTransferredRecordsSearchRequest(
        createdAfterTransfer: Set<String>,
        modifiedAfterTransfer: Set<String>
    ) = SearchRequest(
        "@or @1=1011 @4=106 ${mapTimestampsToQuery(createdAfterTransfer)} "
            + "@1=1012 @4=106 ${mapTimestampsToQuery(modifiedAfterTransfer)}",
        recordSchema = "dc"
    )

    private fun mapTimestampsToQuery(values: Set<String>) = values.joinToString(
        prefix = "\"",
        separator = " ",
        postfix = "\""
    )

    private suspend fun getNotTransferredRecordTimestamps(
        sourceDbId: String,
        scanClause: String
    ): Set<String> {
        val scanResult = dbApiClient.scan(sourceDbId, ScanRequest(scanClause))
        scanResult.data.apply {
            if (!success) {
                throw RuntimeException("Error during scan")
            }
            return data.terms
                ?.map { it.displayTerm }
                ?.toSet()
                ?: setOf()
        }
    }
}