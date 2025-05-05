package com.example.api_tfg.error.exception

class BadRequestException(message: String) : Exception("Bad Request Exception (400). $message") {
}