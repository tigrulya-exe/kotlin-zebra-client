package ru.nsu.manasyan.zebra.model

data class RepositoryDto(
    val id: String,
    val name: String,
    val type: String,
    val databases: List<DatabaseDto>
)

data class RepositoryUpsertDto(
    val name: String,
    val type: String
)