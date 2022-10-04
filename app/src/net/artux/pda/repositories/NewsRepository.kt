package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.ArticleDto
import net.artux.pdanetwork.model.QueryPage
import net.artux.pdanetwork.model.ResponsePageArticleDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class NewsRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val cache: Cache<ArticleDto>,
) {

    fun clearCache() {
        cache.clear()
    }

    fun getCachedArticles(): Result<List<ArticleDto>> {
        return if (cache.all != null)
            Result.success(cache.all)
        else Result.failure(Exception("Note isn't found"))
    }

    suspend fun getArticles(): Result<List<ArticleDto>> {
        return suspendCoroutine {
            webservice.getPageArticles(QueryPage()
                .sortBy("published")
                .sortDirection(QueryPage.SortDirectionEnum.ASC))
                .enqueue(object : Callback<ResponsePageArticleDto> {
                override fun onResponse(
                    call: Call<ResponsePageArticleDto>,
                    response: Response<ResponsePageArticleDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        clearCache()
                        for (article in data.data) {
                            cache.put(article.id.toString(), article)
                        }
                        it.resume(Result.success(data.data))
                    } else
                        it.resume(Result.failure(Exception("Feed null")))
                }

                override fun onFailure(call: Call<ResponsePageArticleDto>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }

            })
        }
    }

}