package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KundaliDao {
    @Query("SELECT COUNT(*) FROM kundali_profiles")
    suspend fun getProfileCount(): Int

    @Query("SELECT * FROM kundali_profiles ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllProfiles(): Flow<List<KundaliEntity>>

    @Query("SELECT * FROM kundali_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): KundaliEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: KundaliEntity): Long

    @Update
    suspend fun updateProfile(profile: KundaliEntity)

    @Delete
    suspend fun deleteProfile(profile: KundaliEntity)

    @Query("DELETE FROM kundali_profiles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
