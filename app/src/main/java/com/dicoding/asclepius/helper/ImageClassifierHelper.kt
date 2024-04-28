package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    var th: Float = 0.1f,
    var max: Int = 1,
    val nameModel: String = "cancer_classification.tflite",
    val cont: Context,
    val classificationListener: ClassifierListener?
) {
    private var imgClassifier: ImageClassifier? = null

    private fun setupImageClassifier() {
        val classifierOptionsBuilder =
            ImageClassifier.ImageClassifierOptions.builder().setScoreThreshold(th)
                .setMaxResults(max)
        val taskBaseOptionsBuilder = BaseOptions.builder().setNumThreads(4)
        classifierOptionsBuilder.setBaseOptions(taskBaseOptionsBuilder.build())

        try {
            imgClassifier = ImageClassifier.createFromFileAndOptions(
                cont, nameModel, classifierOptionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classificationListener?.onError(cont.getString(R.string.failed_to_classify))
            Log.e(TAG, e.message.toString())
        }
    }

    init {
        setupImageClassifier()
    }

    fun classifyImage(bm: Bitmap) {
        imgClassifier ?: setupImageClassifier()

        val imageResizeProcessor =
            ImageProcessor.Builder().add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(CastOp(DataType.UINT8)).build()

        val inputTensorImg = imageResizeProcessor.process(TensorImage.fromBitmap(bm))

        val imageProcessingOptionsBuilder = ImageProcessingOptions.builder()
            .setOrientation(ImageProcessingOptions.Orientation.TOP_LEFT).build()

        var timeMilis = SystemClock.uptimeMillis()
        val res = imgClassifier?.classify(inputTensorImg, imageProcessingOptionsBuilder)
        timeMilis = SystemClock.uptimeMillis() - timeMilis
        classificationListener?.onResults(res, timeMilis)
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            res: List<Classifications>?, timeMilis: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}
