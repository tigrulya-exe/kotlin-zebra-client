package ru.nsu.manasyan.zebra.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.gson.*
import ru.nsu.manasyan.zebra.ServerErrorException

class ZebraApiHttpClient(private val serverUrl: String) {

    private val httpClient = HttpClient(CIO) {
        defaultRequest {
            url(serverUrl)
        }
        install(ContentNegotiation) {
            gson()
        }
    }
    val storages: ZebraStorageApiClient = ZebraStorageApiHttpClient(httpClient)
    val repositories: ZebraRepositoryApiClient = ZebraRepositoryApiHttpClient(httpClient)
    val databases: ZebraDatabaseApiClient = ZebraDatabaseApiHttpClient(httpClient)

    companion object {
        suspend inline fun <reified V> wrapResponse(response: HttpResponse): V {
            return if (response.status == InternalServerError) {
                throw ServerErrorException(response.body())
            } else response.body()
        }
    }
}