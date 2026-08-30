package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kundali_profiles")
data class KundaliEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: String, // YYYY-MM-DD
    val birthTime: String, // HH:mm
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double,
    val locationName: String,
    val gender: String = "Male",
    val notes: String = "",
    val ayanamshaName: String = "LAHIRI",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val cityName: String get() = locationName
    val timezoneOffset: Double get() = timezoneOffsetHours
    val ayanamshaSystem: String get() = ayanamshaName
}
