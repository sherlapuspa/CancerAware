package com.dicoding.asclepius.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapt.OnDeleteClickListener
import com.dicoding.asclepius.adapt.PredictAdapt
import com.dicoding.asclepius.databinding.ActivityBookmarkBinding
import com.dicoding.asclepius.database.CancerPredict
import com.dicoding.asclepius.database.CancerPredictDao
import com.dicoding.asclepius.database.CancerPredictDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity(), OnDeleteClickListener {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var predictionAdapter: PredictAdapt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupPredictionsObserver()

        binding.backButton.setOnClickListener {
            navigateToMain()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        predictionAdapter = PredictAdapt(this)
        binding.recyclerView.adapter = predictionAdapter
    }

    private fun setupPredictionsObserver() {
        val cancerPredictionDao = CancerPredictDb.getDatabase(this)?.cancerPredictionDao()
        cancerPredictionDao?.getAllPredictions()?.observe(this, { predictions ->
            predictions?.let {
                predictionAdapter.setPredictionList(it)
            }
        })
    }

    override fun onDeleteClick(predictionItem: CancerPredict) {
        val cancerPredictionDao = CancerPredictDb.getDatabase(this)?.cancerPredictionDao()
        deletePredictionAsync(cancerPredictionDao, predictionItem)
    }

    private fun deletePredictionAsync(
        cancerPredictionDao: CancerPredictDao?, predictionItem: CancerPredict
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            cancerPredictionDao?.deletePrediction(predictionItem)
            withContext(Dispatchers.Main) {
                predictionAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}