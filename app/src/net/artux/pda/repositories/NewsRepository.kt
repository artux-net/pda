package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.ArticleSimpleDto
import net.artux.pdanetwork.model.QueryPage
import net.artux.pdanetwork.model.ResponsePageArticleSimpleDto
import net.artux.pdanetwork.model.ResponsePageCommentDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class NewsRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val cache: Cache<ArticleSimpleDto>,
) {

    fun clearCache() {
        cache.clear()
    }

    fun getCachedArticles(): Result<List<ArticleSimpleDto>> {
        return if (cache.all != null)
            Result.success(cache.all)
        else Result.failure(Exception("Note isn't found"))
    }

    suspend fun getArticles(): Result<List<ArticleSimpleDto>> {
        return suspendCoroutine {
            webservice.getPageArticles(
                QueryPage()
                    .sortBy("published")
                    .sortDirection(QueryPage.SortDirectionEnum.ASC), emptyList()
            )
                .enqueue(object : Callback<ResponsePageArticleSimpleDto> {
                    override fun onResponse(
                        call: Call<ResponsePageArticleSimpleDto>,
                        response: Response<ResponsePageArticleSimpleDto>
                    ) {
                        val data = response.body()
                        if (data != null) {
                            clearCache()
                            for (article in data.content) {
                                cache.put(article.id.toString(), article)
                            }
                            it.resume(Result.success(data.content))
                        } else
                            it.resume(Result.failure(Exception("Feed null")))
                    }

                    override fun onFailure(call: Call<ResponsePageArticleSimpleDto>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }

                })
        }
    }

    suspend fun likeArticle(articleId: UUID): Result<Boolean> {
        return suspendCoroutine {
            webservice.likeArticle(articleId).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Feed null")))
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    it.resume(Result.failure(t))
                }
            })
        }
    }
}