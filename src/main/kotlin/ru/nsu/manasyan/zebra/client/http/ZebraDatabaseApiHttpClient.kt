package ru.nsu.manasyan.zebra.client.http

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.nsu.manasyan.zebra.client.ZebraDatabaseApiClient
import ru.nsu.manasyan.zebra.client.http.ZebraApiHttpClient.Companion.wrapResponse
import ru.nsu.manasyan.zebra.model.*

class ZebraDatabaseApiHttpClient(private val httpClient: HttpClient) : ZebraDatabaseApiClient {
    companion object {
        const val BASE_URL = "databases"
    }

    override suspend fun create(createDto: DatabaseUpsertDto): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.post {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            setBody(createDto)
        })
    }

    override suspend fun getAll(): Results<DatabaseDto> {
        return wrapResponse(httpClient.get(BASE_URL))
    }

    override suspend fun get(id: String): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.get("$BASE_URL/$id"))
    }

    override suspend fun update(id: String, updateDto: DatabaseUpsertDto): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.put {
            url("$BASE_URL/$id")
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        })
    }

    override suspend fun delete(id: String): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.delete("$BASE_URL/$id"))
    }

    override suspend fun insertStorage(id: String, storageId: String): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.post("$BASE_URL/$id/update/$storageId"))
    }

    override suspend fun drop(id: String): ValueResult<DatabaseDto> {
        return wrapResponse(httpClient.post("$BASE_URL/$id/drop"))
    }

    override suspend fun search(id: String, searchRequest: SearchRequest): ValueResult<ValueResult<SearchResponse>> {
        return wrapResponse(httpClient.get {
            url("$BASE_URL/$id/search")
            parameter("type", searchRequest.type)
            parameter("query", searchRequest.query)
            parameter("startRecord", searchRequest.startRecord)
            parameter("maximumRecords", searchRequest.maximumRecords)
            parameter("recordSchema", searchRequest.recordSchema)
            parameter("sortKeys", searchRequest.sortKeys)
        })
    }

    override suspend fun scan(id: String, scanRequest: ScanRequest): ValueResult<ValueResult<ScanResponse>> {
        return wrapResponse(httpClient.get {
            url("$BASE_URL/$id/scan")
            parameter("type", scanRequest.type)
            parameter("scanClause", scanRequest.scanClause)
            parameter("number", scanRequest.number)
            parameter("position", scanRequest.position)
        })
    }
}