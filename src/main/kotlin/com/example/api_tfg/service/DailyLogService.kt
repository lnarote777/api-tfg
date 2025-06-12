package com.example.api_tfg.service

import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.repository.DailyLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Servicio para gestionar los registros diarios (DailyLog) de los usuarios.
 *
 * Permite crear, obtener, actualizar y eliminar registros diarios,
 * además de recalcular el ciclo menstrual cuando sea necesario.
 *
 * @property dailyLogRepository Repositorio para acceder a los DailyLog en la base de datos.
 * @property menstrualCycleService Servicio para gestionar ciclos menstruales.
 */
@Service
class DailyLogService(
    @Autowired val dailyLogRepository: DailyLogRepository,
    @Autowired val menstrualCycleService: MenstrualCycleService
) {

    /**
     * Crea un nuevo registro diario para un usuario.
     *
     * Valida que no exista ya un registro para la misma fecha.
     * Si el registro indica que no hay menstruación, recalcula el ciclo menstrual.
     *
     * @param userId ID del usuario.
     * @param dto Objeto DTO con los datos del registro diario.
     * @throws IllegalArgumentException si ya existe un registro para esa fecha.
     * @return El registro diario guardado.
     */
    fun createLog(userId: String, dto: DailyLogDTO): DailyLog {
        val existing = dailyLogRepository.findByUserIdAndDate(userId, dto.date)
        if (existing.isPresent  ) throw IllegalArgumentException("Ya hay un log para ese día.")

        val log = DailyLog(
            userId = userId,
            date = dto.date,
            hasMenstruation = dto.hasMenstruation,
            menstrualFlow = dto.menstrualFlow,
            sexualActivity = dto.sexualActivity,
            mood = dto.mood,
            symptoms = dto.symptoms,
            vaginalDischarge = dto.vaginalDischarge,
            physicalActivity = dto.physicalActivity,
            pillsTaken = dto.pillsTaken,
            waterIntake = dto.waterIntake,
            weight = dto.weight,
            notes = dto.notes
        )

        val savedLog = dailyLogRepository.save(log)

        // Recalcular solo si no hay sangrado
        if (!dto.hasMenstruation) {
            val parsedDate = LocalDate.parse(dto.date)
            menstrualCycleService.recalculateCycleIfNoBleeding(userId, parsedDate)
        }

        return savedLog
    }

    /**
     * Obtiene todos los registros diarios de todos los usuarios.
     *
     * @return Lista de todos los registros diarios.
     */
    fun getAllLogs(): List<DailyLog> {
        return dailyLogRepository.findAll()
    }

    /**
     * Obtiene todos los registros diarios de un usuario específico.
     *
     * @param userId ID del usuario.
     * @return Lista de registros diarios del usuario.
     */
    fun getLogsByUser(userId: String): List<DailyLog> {
        return dailyLogRepository.findByUserId(userId)
    }

    /**
     * Obtiene un registro diario de un usuario en una fecha concreta.
     *
     * @param userId ID del usuario.
     * @param date Fecha del registro (formato ISO).
     * @throws NotFoundException si no se encuentra el registro.
     * @return Registro diario correspondiente.
     */
    fun getLogByUserAndDate(userId: String, date: String): DailyLog {
        return dailyLogRepository.findByUserIdAndDate(userId, date).orElseThrow { NotFoundException("No se encontró log p ara el usuario $userId en $date") }
    }

    /**
     * Obtiene un registro diario por su ID.
     *
     * @param id ID del registro.
     * @throws NotFoundException si no se encuentra el registro.
     * @return Registro diario correspondiente.
     */
    fun getLogById(id: String): DailyLog {
        return dailyLogRepository.findById(id)
            .orElseThrow { NotFoundException("Log con id $id no encontrado") }
    }

    /**
     * Actualiza un registro diario existente.
     *
     * Si el registro existe, actualiza sus campos con los datos del DTO y lo guarda.
     * Si no existe, devuelve null.
     *
     * @param id ID del registro a actualizar.
     * @param dto Datos actualizados del registro.
     * @return Registro actualizado o null si no existe.
     */
    fun updateLog(id: String, dto: DailyLogDTO): DailyLog? {
        val existing = dailyLogRepository.findById(id)

        if (existing.isPresent) {
            val existingLog = existing.get()
            val updated = existingLog.copy(
                date = dto.date,
                hasMenstruation = dto.hasMenstruation,
                menstrualFlow = dto.menstrualFlow,
                sexualActivity = dto.sexualActivity,
                mood = dto.mood,
                symptoms = dto.symptoms,
                vaginalDischarge = dto.vaginalDischarge,
                physicalActivity = dto.physicalActivity,
                pillsTaken = dto.pillsTaken,
                waterIntake = dto.waterIntake,
                weight = dto.weight,
                notes = dto.notes
            )
            return dailyLogRepository.save(updated)
        }else{
            return null
        }


    }

    /**
     * Elimina un registro diario por su ID.
     *
     * @param id ID del registro a eliminar.
     * @throws NotFoundException si el registro no existe.
     */
    fun deleteLog(id: String) {
        if (!dailyLogRepository.existsById(id)) {
            throw NotFoundException("Log con id $id no encontrado")
        }
        dailyLogRepository.deleteById(id)
    }

}
