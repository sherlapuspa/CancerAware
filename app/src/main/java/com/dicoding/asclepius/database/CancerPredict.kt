package com.dicoding.asclepius.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predictions")
class CancerPredict(
    val imageUri: String, val label: String, val confidenceScore: Float
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
