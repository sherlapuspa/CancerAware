package com.dicoding.asclepius.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CancerPredictDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: CancerPredict)

    @Query("SELECT * FROM predictions")
    fun getAllPredictions(): LiveData<List<CancerPredict>>

    @Query("DELETE FROM predictions WHERE imageUri = :uri")
    suspend fun deletePredictionByUri(uri: String)

    @Delete
    suspend fun deletePrediction(prediction: CancerPredict)

    @Query("SELECT * FROM predictions WHERE imageUri = :uri")
    suspend fun getPredictionByUri(uri: String): CancerPredict?


}