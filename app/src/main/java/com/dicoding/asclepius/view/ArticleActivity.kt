package com.dicoding.asclepius.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapt.ArticleAdapt
import com.dicoding.asclepius.databinding.ActivityArticleBinding
import com.dicoding.asclepius.vm.ArticleVm

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding
    private lateinit var vm: ArticleVm
    private lateinit var adapt: ArticleAdapt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapt = ArticleAdapt(this)
        adapt.notifyDataSetChanged()
        vm = ViewModelProvider(this).get(ArticleVm::class.java)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@ArticleActivity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapt
        }

        binding.backButton.setOnClickListener { navigateToMain() }

        vm.getTopHl().observe(this, { newsList ->
            adapt.setArticleList(ArrayList(newsList))
            binding.progressBar.visibility = View.GONE
        })

        vm.isLoad.observe(this, { isLoad ->
            binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
        })

        vm.setTopHl("cancer", "health", "en", "7b5627c4e83946b7946a1659108c0777")
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}