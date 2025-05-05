package com.example.api_tfg.error.exception

class UnauthorizedException(message: String) : Exception("Not authorized exception (401). $message") {
}