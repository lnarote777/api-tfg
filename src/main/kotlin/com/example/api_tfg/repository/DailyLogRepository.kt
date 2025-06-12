package com.example.api_tfg.repository

import com.example.api_tfg.model.DailyLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional

/**
 * Repositorio para gestionar la persistencia de registros diarios (`DailyLog`) en MongoDB.
 */
@Repository
interface DailyLogRepository: MongoRepository<DailyLog, String> {
    /**
     * Busca todos los registros diarios asociados a un usuario específico.
     *
     * @param userId Identificador del usuario.
     * @return Lista de registros diarios del usuario.
     */
    fun findByUserId(userId: String): List<DailyLog>

    /**
     * Busca un registro diario específico de un usuario en una fecha determinada.
     *
     * @param userId Identificador del usuario.
     * @param date Fecha del registro en formato String.
     * @return Optional que contiene el registro diario si existe, o vacío en caso contrario.
     */
    fun findByUserIdAndDate(userId: String, date: String): Optional<DailyLog>
}