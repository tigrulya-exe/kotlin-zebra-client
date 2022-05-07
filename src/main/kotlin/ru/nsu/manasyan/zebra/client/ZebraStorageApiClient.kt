package ru.nsu.manasyan.zebra.client

import ru.nsu.manasyan.zebra.model.Results
import ru.nsu.manasyan.zebra.model.StorageDto
import ru.nsu.manasyan.zebra.model.UpdateStorageDto
import ru.nsu.manasyan.zebra.model.ValueResult
import java.io.File

interface ZebraStorageApiClient {
    suspend fun create(databaseId: String, data: File, additionalInfo: String? = null): ValueResult<StorageDto>

    suspend fun getAll(): Results<StorageDto>

    suspend fun get(id: String): ValueResult<StorageDto>

    suspend fun update(id: String, updateDto: UpdateStorageDto): ValueResult<StorageDto>

    suspend fun delete(id: String): ValueResult<StorageDto>

    suspend fun deleteAll(): List<ValueResult<StorageDto>>
}