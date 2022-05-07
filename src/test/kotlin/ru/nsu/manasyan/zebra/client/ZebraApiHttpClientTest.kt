package ru.nsu.manasyan.zebra.client

import kotlinx.coroutines.runBlocking
import ru.nsu.manasyan.zebra.client.TestUtils.clearAllReposRecursively
import ru.nsu.manasyan.zebra.client.TestUtils.createDb
import ru.nsu.manasyan.zebra.client.TestUtils.createRepo
import ru.nsu.manasyan.zebra.client.TestUtils.createStorage
import ru.nsu.manasyan.zebra.client.http.ZebraApiHttpClient
import ru.nsu.manasyan.zebra.model.*
import java.io.File
import kotlin.test.Test

internal class ZebraApiHttpClientTest {
    private val zebraApi = ZebraApiHttpClient("http://localhost:3000/api/v1/")

    @Test
    fun checkHappyPath(): Unit = runBlocking {

        val repoInfo = createRepo("r1", "fit.nsu.ru")

        val dbInfo = createDb(repoInfo.id, "db1")

        createStorage(
            dbInfo.id,
            "src/test/resources/collectionDb1.xml"
        )

        val searchResult = zebraApi.databases.search(
            id = dbInfo.id,
            searchRequest = SearchRequest(
                query = "dc.creator=Пальчунов or dc.creator=Зюбин",
                type = QueryType.CQL,
                recordSchema = "dc"
            )
        )
        assert(searchResult.success)

        val scanResult = zebraApi.databases.scan(
            id = dbInfo.id,
            scanRequest = ScanRequest(
                scanClause = "dc.coverage=Novosibirsk",
                type = QueryType.CQL,
            )
        )
        assert(scanResult.success)

        clearAllReposRecursively()
    }
}

object TestUtils {
    val zebraApi = ZebraApiHttpClient("http://localhost:3000/api/v1/")

    suspend fun createRepo(name: String, type: String): RepositoryDto {
        val repoCreateResponse = zebraApi.repositories.create(
            RepositoryUpsertDto(
                name = name,
                type = type
            )
        )
        assert(repoCreateResponse.success)
        assert(zebraApi.repositories.init(repoCreateResponse.data.id).success)
        return repoCreateResponse.data
    }

    suspend fun createDb(repositoryId: String, dbName: String): DatabaseDto {
        val dbCreateResponse = zebraApi.databases.create(
            DatabaseUpsertDto(
                repositoryId = repositoryId,
                name = dbName
            ),
        )
        assert(dbCreateResponse.success)
        assert(zebraApi.repositories.commit(repositoryId).success)
        return dbCreateResponse.data
    }

    suspend fun createStorage(
        dbId: String,
        fileName: String,
        autoInsert: Boolean = true
    ): StorageDto {
        val storageCreateResponse = zebraApi.storages.create(
            databaseId = dbId,
            data = File(fileName)
        )
        assert(storageCreateResponse.success)

        if (autoInsert) {
            assert(
                zebraApi.databases.insertStorage(
                    id = dbId,
                    storageId = storageCreateResponse.data.id
                ).success
            )
        }
        return storageCreateResponse.data
    }

    suspend fun clearAllReposRecursively() {
        zebraApi.apply {
            checkSuccess(repositories.deleteAll())
            checkSuccess(databases.deleteAll())
            checkSuccess(storages.deleteAll())
        }
    }

    private fun <T> checkSuccess(results: List<ValueResult<T>>) = results.all { it.success }
}