package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var thisImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener(this::startGallery)
        binding.analyzeButton.setOnClickListener(this::navigateToResult)
        binding.deleteImgButton.setOnClickListener(this::deleteImg)
        binding.bookmarkButton.setOnClickListener(this::navigateToBookmark)
        binding.cancerArticleButton.setOnClickListener(this::navigateToCancerArticle)
    }

    private fun startGallery(view: View) {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        imgCropLauncher.launch(intent)
    }

    private val imgCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val imgUri: Uri? = result.data?.data
                    imgUri?.let {
                        launchImgCrop(it)
                    } ?: Log.d(
                        "Photo Cropping",
                        "No media was selected for cropping. Please select an image to crop."
                    )
                }

                else -> {
                    Log.d("Photo Cropping", "Activity result not OK")
                }
            }
        }

    private val imgUCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val croppedUri = UCrop.getOutput(result.data!!)
                if (croppedUri != null) {
                    thisImgUri = croppedUri
                    showImage()
                    deleteButtonOn()
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                showToast("Error: ${error?.localizedMessage}")
            }
        }

    private fun launchImgCrop(uri: Uri) {
        val imgName = "${System.currentTimeMillis()}_cropped_image.jpg"
        val uriDest = Uri.fromFile(File(cacheDir, imgName))

        val intent = UCrop.of(uri, uriDest).withAspectRatio(1f, 1f).getIntent(this)

        imgUCropLauncher.launch(intent)
    }

    private fun showImage() {
        Glide.with(this).load(thisImgUri ?: R.drawable.addpicture)
            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            .into(binding.previewImageView)
    }

    private fun navigateToResult(view: View) {
        val intent = thisImgUri?.let { uri ->
            Intent(this, ResultActivity::class.java).apply {
                putExtra(EXTRA_IMG, uri)
            }
        }
        intent?.let {
            startActivity(it)
        } ?: showToast("Please select an image to crop first")
    }

    private fun navigateToBookmark(view: View) {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCancerArticle(view: View) {
        val intent = Intent(this, ArticleActivity::class.java)
        startActivity(intent)
    }

    private fun deleteImg(view: View) {
        thisImgUri = null
        Glide.with(this).load(R.drawable.addpicture).into(binding.previewImageView)
        binding.deleteImgButton.visibility = View.GONE
    }

    private fun deleteButtonOn() {
        binding.deleteImgButton.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMG = "com.dicoding.asclepius.view.EXTRA_IMG"
    }
}