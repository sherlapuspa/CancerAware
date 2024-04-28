package com.dicoding.asclepius.adapt

import com.dicoding.asclepius.database.CancerPredict

interface OnDeleteClickListener {
    fun onDeleteClick(prediction: CancerPredict)
}