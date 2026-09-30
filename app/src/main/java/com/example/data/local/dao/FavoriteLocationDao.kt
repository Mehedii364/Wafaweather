package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {
    @Query("SELECT * FROM favorite_locations ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(location: FavoriteLocationEntity): Long

    @Update
    suspend fun updateFavorite(location: FavoriteLocationEntity)

    @Delete
    suspend fun deleteFavorite(location: FavoriteLocationEntity)

    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM favorite_locations WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    suspend fun findByCoordinates(lat: Double, lon: Double): FavoriteLocationEntity?

    @Query("SELECT COUNT(*) FROM favorite_locations")
    suspend fun count(): Int
}
