package ru.nsu.manasyan.zebra.client

import kotlinx.coroutines.runBlocking
import ru.nsu.manasyan.zebra.model.DatabaseUpsertDto
import ru.nsu.manasyan.zebra.model.RepositoryUpsertDto
import ru.nsu.manasyan.zebra.model.SearchRequest
import java.io.File
import kotlin.test.Test

internal class ZebraApiHttpClientTest {
    private val zebraApi = ZebraApiHttpClient("http://localhost:3000/api/v1/")

    @Test
    fun checkHappyPath(): Unit = runBlocking {
        val repoCreateResponse = zebraApi.repositories.create(
            RepositoryUpsertDto(
                name = "r1",
                type = "fit.nsu.ru"
            )
        )
        assert(repoCreateResponse.success)
        assert(zebraApi.repositories.init(repoCreateResponse.data.id).success)

        val dbCreateResponse = zebraApi.databases.create(
            DatabaseUpsertDto(
                repositoryId = repoCreateResponse.data.id,
                name = "db1"
            )
        )
        assert(dbCreateResponse.success)


        val storageCreateResponse = zebraApi.storages.create(
            databaseId = dbCreateResponse.data.id,
            data = File("src/test/resources/collection.xml")
        )
        assert(storageCreateResponse.success)

        assert(
            zebraApi.databases.insertStorage(
                id = dbCreateResponse.data.id,
                storageId = storageCreateResponse.data.id
            ).success
        )

        val searchResult = zebraApi.databases.search(
            id = dbCreateResponse.data.id,
            searchRequest = SearchRequest(
                query = "@1=4 нгу"
            )
        )
        assert(searchResult.success)

        println(searchResult.data)

        assert(zebraApi.storages.delete(storageCreateResponse.data.id).success)
        assert(zebraApi.databases.delete(dbCreateResponse.data.id).success)
        assert(zebraApi.repositories.delete(repoCreateResponse.data.id).success)
    }
}