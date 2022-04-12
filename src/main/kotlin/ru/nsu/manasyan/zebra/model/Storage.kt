package ru.nsu.manasyan.zebra.model

import com.google.gson.annotations.SerializedName

data class StorageDto(
    val id: String,
    @SerializedName("database_id")
    val databaseId: String,
    @SerializedName("uuidfilename")
    val uuidFilename: String,
    val filename: String,
    val filesize: Int,
    val addInfo: String? = null
)


data class UpdateStorageDto(
    val addInfo: String
)