package com.dicoding.asclepius.adapt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.databinding.ArticleItemBinding
import com.dicoding.asclepius.model.DataArticle

class ArticleAdapt(private val context: Context) :
    RecyclerView.Adapter<ArticleAdapt.ArticlesViewHolder>() {
    private val articleList = ArrayList<DataArticle>()

    fun setArticleList(articles: ArrayList<DataArticle>) {
        articleList.clear()
        articleList.addAll(articles.filter { article ->
            article.title != "[Removed]" && article.description != "[Removed]" && article.urlToImage != null
        })
        notifyDataSetChanged()
    }

    inner class ArticlesViewHolder(val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cancerArticle: DataArticle) {
            binding.apply {
                Glide.with(itemView).load(cancerArticle.urlToImage).centerCrop().into(articlePict)

                tvArticleTitle.text = cancerArticle.title
                tvArticleAuthor.text = cancerArticle.author
                tvArticlePublisher.text = cancerArticle.publishedAt
                tvArticleDesc.text = cancerArticle.description

                ibReadMore.setOnClickListener {
                    val cancerArticleUrl = cancerArticle.url
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cancerArticleUrl))
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val view = ArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticlesViewHolder(view)
    }

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        holder.bind(articleList[position])
    }

}