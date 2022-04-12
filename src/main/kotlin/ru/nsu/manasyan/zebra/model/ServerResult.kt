package ru.nsu.manasyan.zebra.model

sealed interface ServerResult

data class ErrorDto(
    val code: String,
    val message: String
) : ServerResult

data class ValueResult<V>(
    val success: Boolean,
    val data: V
) : ServerResult

data class Results<V>(
    val success: Boolean,
    val data: List<V>
)
