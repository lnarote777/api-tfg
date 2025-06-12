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

/**
 * Manejador global de excepciones para la API.
 *
 * Intercepta excepciones lanzadas en los controladores y responde con un
 * mensaje estructurado y el código HTTP correspondiente.
 */
@ControllerAdvice
class APIExceptionHandler {

    /**
     * Maneja excepciones relacionadas con la autenticación y autorización.
     *
     * Devuelve un código HTTP 401 (UNAUTHORIZED) y un cuerpo con el mensaje de error y la URI.
     *
     * @param request Objeto HttpServletRequest para obtener información de la petición.
     * @param e Excepción capturada.
     * @return Objeto ErrorRespuesta con detalles del error.
     */
    @ExceptionHandler(
        AuthenticationException::class,
        UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun handleAuthentication(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    /**
     * Maneja excepciones genéricas y NullPointerException.
     *
     * Devuelve un código HTTP 500 (INTERNAL_SERVER_ERROR).
     *
     * @param request Objeto HttpServletRequest para obtener información de la petición.
     * @param e Excepción capturada.
     * @return Objeto ErrorRespuesta con detalles del error.
     */
    @ExceptionHandler(Exception::class, NullPointerException::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGeneric(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    /**
     * Maneja excepciones relacionadas con peticiones mal formadas o usuarios duplicados.
     *
     * Devuelve un código HTTP 400 (BAD_REQUEST).
     *
     * @param request Objeto HttpServletRequest para obtener información de la petición.
     * @param e Excepción capturada.
     * @return Objeto ErrorRespuesta con detalles del error.
     */
    @ExceptionHandler(
        BadRequestException::class,
        UserExistException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBadRequest(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    /**
     * Maneja excepciones cuando un recurso no es encontrado.
     *
     * Devuelve un código HTTP 404 (NOT_FOUND).
     *
     * @param request Objeto HttpServletRequest para obtener información de la petición.
     * @param e Excepción capturada.
     * @return Objeto ErrorRespuesta con detalles del error.
     */
    @ExceptionHandler(
        NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleNotFound(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }
}