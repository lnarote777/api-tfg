package com.example.api_tfg.error.exception

class UserExistException (message: String) : Exception("User Exist (400). $message") {
}