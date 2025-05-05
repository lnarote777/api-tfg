package com.example.api_tfg.error


import com.example.api_tfg.error.exception.BadRequestException
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.error.exception.UnauthorizedException
import com.example.api_tfg.error.exception.UserExistException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.naming.AuthenticationException

@ControllerAdvice
class APIExceptionHandler {

    @ExceptionHandler(
        AuthenticationException::class,
        UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun handleAuthentication(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(Exception::class, NullPointerException::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGeneric(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(
        BadRequestException::class,
        UserExistException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBadRequest(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(
        NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleNotFound(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }
}