package com.example.api_tfg.repository

import com.example.api_tfg.model.MenstrualCycle
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para gestionar la persistencia de ciclos menstruales (`MenstrualCycle`) en MongoDB.
 */
@Repository
interface MenstrualCycleRepository: MongoRepository<MenstrualCycle, String> {
    /**
     * Busca todos los ciclos menstruales asociados a un usuario específico.
     *
     * @param userId Identificador del usuario.
     * @return Lista de ciclos menstruales del usuario.
     */
    fun findByUserId(userId: String): List<MenstrualCycle>
    /**
     * Busca el ciclo menstrual más reciente de un usuario, ordenado por fecha de inicio descendente.
     *
     * @param userId Identificador del usuario.
     * @return El ciclo menstrual más reciente o null si no existe.
     */
    fun findTopByUserIdOrderByStartDateDesc(userId: String): MenstrualCycle?
}