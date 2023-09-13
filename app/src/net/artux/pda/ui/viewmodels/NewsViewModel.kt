package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.ArticleMapper
import net.artux.pda.model.news.ArticleModel
import net.artux.pda.repositories.NewsRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    var repository: NewsRepository
) : ViewModel() {
    var articles: MutableLiveData<List<ArticleModel>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var articleMapper: ArticleMapper = ArticleMapper.INSTANCE

    fun update() {
        viewModelScope.launch {
            repository.getArticles()
                .map { articleMapper.models(it) }
                .onSuccess { it ->
                    it.sortBy { it.published }
                    it.reverse()
                    articles.postValue(it)
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun updateFromCache() {
        articles.postValue(
            repository.getCachedArticles()
                .map { articleMapper.models(it) }
                .getOrDefault(Collections.emptyList()))
    }

    fun likeArticle(uuid: UUID){
        viewModelScope.launch {
            repository.likeArticle(uuid)
                .onSuccess { status.postValue(StatusModel("$it")) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }


}