package com.dicoding.asclepius.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CancerPredict::class], version = 1)
abstract class CancerPredictDb : RoomDatabase() {

    abstract fun cancerPredictionDao(): CancerPredictDao

    companion object {
        @Volatile
        var INSTANCE: CancerPredictDb? = null

        fun getDatabase(context: Context): CancerPredictDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, CancerPredictDb::class.java, "prediction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}