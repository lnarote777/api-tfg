package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

/**
 * Representa un registro diario de seguimiento menstrual y hábitos relacionados.
 *
 * @property id Identificador único del registro (asignado por la base de datos).
 * @property userId Identificador del usuario al que pertenece este registro.
 * @property date Fecha del registro en formato String (por ejemplo, "yyyy-MM-dd").
 * @property hasMenstruation Indica si hubo menstruación ese día.
 * @property menstrualFlow Nivel del flujo menstrual en ese día (puede ser nulo si no hay menstruación).
 * @property sexualActivity Lista con las actividades sexuales registradas ese día.
 * @property mood Lista con los estados de ánimo registrados ese día.
 * @property symptoms Lista con los síntomas reportados ese día.
 * @property vaginalDischarge Lista con tipos de flujo vaginal observados ese día.
 * @property physicalActivity Lista con actividades físicas realizadas ese día.
 * @property pillsTaken Lista con las pastillas o medicamentos tomados ese día.
 * @property waterIntake Cantidad de agua ingerida en litros (puede ser nulo si no se registró).
 * @property weight Peso del usuario ese día (puede ser nulo si no se registró).
 * @property notes Notas adicionales que el usuario quiera dejar.
 */
@Document("DailyLog")
data class DailyLog(
    @BsonId
    val id: String? = null,
    val userId: String,
    val date: String,
    val hasMenstruation: Boolean = false,
    val menstrualFlow: MenstrualFlowLevel? = null,
    val sexualActivity:  MutableList<String?> = mutableListOf(),
    val mood:  MutableList<String?> = mutableListOf(),
    val symptoms:  MutableList<String?> = mutableListOf(),
    val vaginalDischarge: MutableList<String?> = mutableListOf(),
    val physicalActivity: MutableList<String?> = mutableListOf(),
    val pillsTaken:  MutableList<String?> = mutableListOf(),
    val waterIntake: Double? = null,
    val weight: Double? = null,
    val notes: String? = null
)