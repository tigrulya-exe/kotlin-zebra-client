package ru.nsu.manasyan.zebra.client.http

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.nsu.manasyan.zebra.client.ZebraRepositoryApiClient
import ru.nsu.manasyan.zebra.client.http.ZebraApiHttpClient.Companion.wrapResponse
import ru.nsu.manasyan.zebra.model.RepositoryDto
import ru.nsu.manasyan.zebra.model.RepositoryUpsertDto
import ru.nsu.manasyan.zebra.model.Results
import ru.nsu.manasyan.zebra.model.ValueResult

class ZebraRepositoryApiHttpClient(private val httpClient: HttpClient) : ZebraRepositoryApiClient {
    companion object {
        const val BASE_URL = "repositories"
    }

    override suspend fun create(createDto: RepositoryUpsertDto): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.post {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            setBody(createDto)
        })
    }

    override suspend fun getAll(): Results<RepositoryDto> {
        return wrapResponse(httpClient.get(BASE_URL))
    }

    override suspend fun get(id: String): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.get("$BASE_URL/$id"))
    }

    override suspend fun update(id: String, updateDto: RepositoryUpsertDto): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.put {
            url("$BASE_URL/$id")
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        })
    }

    override suspend fun delete(id: String): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.delete("$BASE_URL/$id"))
    }

    override suspend fun deleteAll(): List<ValueResult<RepositoryDto>> =
        getAll().data.map { delete(it.id) }

    override suspend fun init(id: String): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.post("$BASE_URL/$id/init"))
    }

    override suspend fun commit(id: String): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.post("$BASE_URL/$id/commit"))
    }

    override suspend fun clean(id: String): ValueResult<RepositoryDto> {
        return wrapResponse(httpClient.post("$BASE_URL/$id/clean"))
    }

    override suspend fun count(): ValueResult<Int> {
        return wrapResponse(httpClient.get("$BASE_URL/count"))
    }
}