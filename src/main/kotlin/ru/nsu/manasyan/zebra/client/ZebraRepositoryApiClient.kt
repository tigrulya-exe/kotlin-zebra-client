package ru.nsu.manasyan.zebra.client

import ru.nsu.manasyan.zebra.model.RepositoryDto
import ru.nsu.manasyan.zebra.model.RepositoryUpsertDto
import ru.nsu.manasyan.zebra.model.Results
import ru.nsu.manasyan.zebra.model.ValueResult

interface ZebraRepositoryApiClient {
    suspend fun create(createDto: RepositoryUpsertDto): ValueResult<RepositoryDto>

    suspend fun getAll(): Results<RepositoryDto>

    suspend fun get(id: String): ValueResult<RepositoryDto>

    suspend fun update(id: String, updateDto: RepositoryUpsertDto): ValueResult<RepositoryDto>

    suspend fun delete(id: String): ValueResult<RepositoryDto>

    suspend fun deleteAll(): List<ValueResult<RepositoryDto>>

    suspend fun init(id: String): ValueResult<RepositoryDto>

    suspend fun commit(id: String): ValueResult<RepositoryDto>

    suspend fun clean(id: String): ValueResult<RepositoryDto>

    suspend fun count(): ValueResult<Int>
}