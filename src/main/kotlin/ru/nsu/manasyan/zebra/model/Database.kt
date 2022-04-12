package ru.nsu.manasyan.zebra.model

import com.google.gson.annotations.SerializedName

data class DatabaseDto(
    val id: String,
    @SerializedName("repository_id")
    val repositoryId: String,
    val name: String,
    val storages: List<StorageDto>
)

data class DatabaseUpsertDto(
    @SerializedName("repository_id")
    val repositoryId: String,
    val name: String
)