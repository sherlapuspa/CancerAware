package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.database.CancerPredict
import com.dicoding.asclepius.database.CancerPredictDb
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imgClassifierHelper: ImageClassifierHelper
    private lateinit var imgUri: Uri
    private var label: String = ""
    private var score: Float = 0.0f
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgUri = intent.getParcelableExtra<Uri>(MainActivity.EXTRA_IMG) ?: return

        imgUri?.let {
            val contentInput = contentResolver.openInputStream(it)
            val bm = BitmapFactory.decodeStream(contentInput)
            binding.resultImage.setImageBitmap(bm)

            imgClassifierHelper = ImageClassifierHelper(cont = this,
                classificationListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ResultActivity, "Failed To Classify", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResults(
                        res: List<Classifications>?, timeMilis: Long
                    ) {
                        res?.takeIf { it.isNotEmpty() && it[0].categories.isNotEmpty() }
                            ?.let { classifications ->
                                val orderedPredictionCategories =
                                    classifications[0].categories.sortedByDescending { it?.score }
                                val formattedResultText =
                                    orderedPredictionCategories.joinToString("\n") { classification ->
                                        val formattedScore =
                                            String.format("%.2f%%", classification.score * 100)
                                        "${classification.label} $formattedScore"
                                    }
                                binding.resultAnalysis.text = formattedResultText

                                label = orderedPredictionCategories.first().label
                                score = orderedPredictionCategories.first().score
                            }
                    }
                })

            imgClassifierHelper.classifyImage(bm)
        }

        binding.bookmarkToogle.setOnCheckedChangeListener { buttonView, isChecked ->
            isBookmarked = isChecked
            updateBookmarkButton()
            keepToDb()
        }

        updateBookmarkButton()

        binding.backButton.setOnClickListener {
            navigateToMain()
        }

        binding.analyzeButton.setOnClickListener {
            navigateToMain()
        }

    }

    private fun updateBookmarkButton() {
        val drawableResId = if (isBookmarked) R.drawable.bookmarkyes else R.drawable.bookmarkno
        binding.bookmarkToogle.setBackgroundResource(drawableResId)
    }

    private fun keepToDb() {
        imgUri?.let { uri ->
            lifecycleScope.launch {
                CancerPredictDb.getDatabase(this@ResultActivity)?.cancerPredictionDao()?.apply {
                    if (isBookmarked) {
                        insertPrediction(CancerPredict(uri.toString(), label, score))
                    } else {
                        deletePredictionByUri(uri.toString())
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}