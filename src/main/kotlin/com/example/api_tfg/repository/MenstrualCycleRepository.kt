package com.example.api_tfg.repository

import com.example.api_tfg.model.MenstrualCycle
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MenstrualCycleRepository: MongoRepository<MenstrualCycle, Int> {
}