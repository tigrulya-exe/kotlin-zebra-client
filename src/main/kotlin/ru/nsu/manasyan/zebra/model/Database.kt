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

data class UpdateRecordRequest(
    val record: String,
    val action: Action = Action.UPDATE,
    val commitEnable: Boolean = true
) {
    enum class Action {
        @SerializedName("update")
        UPDATE,
        @SerializedName("delete")
        DELETE
    }
}