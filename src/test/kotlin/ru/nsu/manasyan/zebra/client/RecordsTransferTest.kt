package ru.nsu.manasyan.zebra.client

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import ru.nsu.manasyan.zebra.client.TestUtils.clearAllReposRecursively
import ru.nsu.manasyan.zebra.client.TestUtils.createDb
import ru.nsu.manasyan.zebra.client.TestUtils.createRepo
import ru.nsu.manasyan.zebra.client.TestUtils.createStorage
import ru.nsu.manasyan.zebra.client.TestUtils.zebraApi
import ru.nsu.manasyan.zebra.model.QueryType
import ru.nsu.manasyan.zebra.model.SearchRequest
import ru.nsu.manasyan.zebra.service.ZebraRecordsTransferService
import ru.nsu.manasyan.zebra.storage.JsonFileTimestampStorage
import kotlin.test.assertEquals

internal class RecordsTransferTest {

    private val recordsTransferService = ZebraRecordsTransferService(
        JsonFileTimestampStorage(),
        zebraApi.databases
    )

    @Test
    fun checkHappyPath(): Unit = runBlocking {
        val repoInfo = createRepo("r1", "fit.nsu.ru")

        val db1Info = createDb(repoInfo.id, "db1")
        createStorage(
            db1Info.id,
            "src/test/resources/collectionDb1.xml"
        )

        val db2Info = createDb(repoInfo.id, "db2")
        createStorage(
            db2Info.id,
            "src/test/resources/collectionDb2.xml"
        )

        val db3Info = createDb(repoInfo.id, "db3")
        recordsTransferService.transferRecords(
            listOf(db1Info.id, db2Info.id),
            db3Info.id
        )

        val db3SearchResponse = zebraApi.databases.search(
            db3Info.id,
            SearchRequest(
                "@1=4 нгу",
                QueryType.PQF,
                recordSchema = "dc"
            )
        )
        assert(db3SearchResponse.data.success)
        assertEquals(4, db3SearchResponse.data.data.records?.size)

        clearAllReposRecursively()
    }
}