package com.smarthelmet.emergency.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smarthelmet.emergency.data.model.IncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestampMillis DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE incidentId = :id LIMIT 1")
    suspend fun getIncidentById(id: String): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    @Query("DELETE FROM incidents WHERE incidentId = :id")
    suspend fun deleteIncident(id: String)

    @Query("DELETE FROM incidents")
    suspend fun clearAll()
}
