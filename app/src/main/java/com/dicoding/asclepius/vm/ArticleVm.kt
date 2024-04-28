package com.dicoding.asclepius.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.model.DataArticle
import com.dicoding.asclepius.model.DataArticleResponse
import com.dicoding.asclepius.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleVm : ViewModel() {

    val articleListData = MutableLiveData<List<DataArticle>>()
    val isLoad = MutableLiveData<Boolean>()

    fun setTopHl(query: String, category: String, language: String, apikey: String) {
        isLoad.value = true

        ApiClient.apiInst.getTopHl("cancer", "health", "en", "9b6a017c23704abf922d67e79f283651")
            .enqueue(object : Callback<DataArticleResponse> {
                override fun onResponse(
                    call: Call<DataArticleResponse>, response: Response<DataArticleResponse>
                ) {
                    isLoad.postValue(false)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val articles = responseBody.articles
                            articleListData.postValue(articles)
                        } else {
                            Log.e("ArticleVm", "Response body is null")
                        }
                    } else {
                        Log.e("ArticleVm", "Unsuccessful response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DataArticleResponse>, t: Throwable) {
                    Log.e("MainActivity", "onFailure: ${t.message}")
                    isLoad.postValue(false)
                }

            })
    }

    fun getTopHl(): LiveData<List<DataArticle>> {
        return articleListData
    }
}