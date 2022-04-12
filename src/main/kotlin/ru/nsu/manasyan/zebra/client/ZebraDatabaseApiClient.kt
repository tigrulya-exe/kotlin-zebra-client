package ru.nsu.manasyan.zebra.client

import ru.nsu.manasyan.zebra.model.*

interface ZebraDatabaseApiClient {
    suspend fun create(createDto: DatabaseUpsertDto): ValueResult<DatabaseDto>

    suspend fun getAll(): Results<DatabaseDto>

    suspend fun get(id: String): ValueResult<DatabaseDto>

    suspend fun update(id: String, updateDto: DatabaseUpsertDto): ValueResult<DatabaseDto>

    suspend fun insertStorage(id: String, storageId: String): ValueResult<DatabaseDto>

    suspend fun delete(id: String): ValueResult<DatabaseDto>

    suspend fun drop(id: String): ValueResult<DatabaseDto>

    suspend fun search(id: String, searchRequest: SearchRequest): ValueResult<ValueResult<SearchResponse>>

    suspend fun scan(id: String, scanRequest: ScanRequest): ValueResult<ValueResult<ScanResponse>>
}