package com.example.api_tfg.error.exception

class NotFoundException  (message: String) : Exception("Not Found (404). $message") {
}