package com.dicoding.asclepius.adapt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.database.CancerPredict
import com.dicoding.asclepius.R

class PredictAdapt(private val clickListener: OnDeleteClickListener) :
    RecyclerView.Adapter<PredictAdapt.cancerPredictViewHolder>() {
    private var cancerPredictList: List<CancerPredict> = emptyList()

    inner class cancerPredictViewHolder(
        itemView: View, private val clickListener: OnDeleteClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        val labelClassTv: TextView = itemView.findViewById(R.id.tvLabelClass)
        val scorePredictTv: TextView = itemView.findViewById(R.id.tvViewConfScore)
        val imageView: ImageView = itemView.findViewById(R.id.imageView1)
        val deleteItem: ImageView = itemView.findViewById(R.id.deleteItem)

        init {
            deleteItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val predicted = cancerPredictList[position]
                    clickListener.onDeleteClick(predicted)
                }
            }
        }
    }

    fun setPredictionList(predictions: List<CancerPredict>) {
        cancerPredictList = predictions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cancerPredictViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.predict_item, parent, false)
        return cancerPredictViewHolder(itemView, clickListener)
    }

    override fun onBindViewHolder(holder: cancerPredictViewHolder, position: Int) {
        val thisPrediction = cancerPredictList[position]
        holder.labelClassTv.text = thisPrediction.label

        val confidenceScore = thisPrediction.confidenceScore * 100
        val confidenceToString = "%.2f%%".format(confidenceScore)
        holder.scorePredictTv.text = confidenceToString

        Glide.with(holder.itemView).load(thisPrediction.imageUri).into(holder.imageView)
    }

    override fun getItemCount() = cancerPredictList.size
}