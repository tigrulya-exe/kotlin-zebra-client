package ru.nsu.manasyan.zebra.client

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import ru.nsu.manasyan.zebra.client.ZebraApiHttpClient.Companion.wrapResponse
import ru.nsu.manasyan.zebra.model.Results
import ru.nsu.manasyan.zebra.model.StorageDto
import ru.nsu.manasyan.zebra.model.UpdateStorageDto
import ru.nsu.manasyan.zebra.model.ValueResult
import java.io.File

class ZebraStorageApiHttpClient(private val httpClient: HttpClient) : ZebraStorageApiClient {
    companion object {
        val BASE_URL = "storages"
    }

    override suspend fun create(databaseId: String, data: File, additionalInfo: String?): ValueResult<StorageDto> {
        return httpClient.submitFormWithBinaryData(
            url = BASE_URL,
            formData = formData {
                append("database_id", databaseId)
                append("data", data.readBytes(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=${data.name}")
                })
                additionalInfo?.let { append("addInfo", it) }
            }
        ).let { wrapResponse(it) }
    }

    override suspend fun getAll(): Results<StorageDto> {
        return wrapResponse(httpClient.get(BASE_URL))
    }

    override suspend fun get(id: String): ValueResult<StorageDto> {
        return wrapResponse(httpClient.get("$BASE_URL/$id"))
    }

    override suspend fun update(id: String, updateDto: UpdateStorageDto): ValueResult<StorageDto> {
        return wrapResponse(httpClient.put {
            url("$BASE_URL/$id")
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        })
    }


    override suspend fun delete(id: String): ValueResult<StorageDto> {
        return wrapResponse(httpClient.delete("$BASE_URL/$id"))
    }
}