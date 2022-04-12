package ru.nsu.manasyan.zebra

import ru.nsu.manasyan.zebra.model.ErrorDto

class ServerErrorException(
    val errorDto: ErrorDto
) : RuntimeException()