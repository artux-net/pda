package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.CommentCreateDto
import net.artux.pdanetwork.model.CommentDto
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
class CommentsRepository @Inject constructor(
    private val webservice: DefaultApi
) {

    enum class CommentType{
        ARTICLE,
        POST
    }

    suspend fun likeComment(commentId: UUID): Result<Boolean> {
        return suspendCoroutine {
            webservice.likeComment(commentId).enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
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

    suspend fun getComments(commentType: CommentType, id: UUID, page: Int): Result<ResponsePageCommentDto> {
        return suspendCoroutine {
            webservice.getComments(commentType.toString(), id, page, null,null, null).enqueue(object : Callback<ResponsePageCommentDto> {
                override fun onResponse(
                    call: Call<ResponsePageCommentDto>,
                    response: Response<ResponsePageCommentDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Не удалось загрузить комментарии")))
                }

                override fun onFailure(call: Call<ResponsePageCommentDto>, t: Throwable) {
                    it.resume(Result.failure(t))
                }
            })
        }
    }

    suspend fun leaveComment(commentType: CommentType, id: UUID, content: String): Result<CommentDto> {
        return suspendCoroutine {
            webservice.commentPost(CommentCreateDto().content(content),commentType.toString(), id).enqueue(object : Callback<CommentDto> {
                override fun onResponse(
                    call: Call<CommentDto>,
                    response: Response<CommentDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Feed null")))
                }

                override fun onFailure(call: Call<CommentDto>, t: Throwable) {
                    it.resume(Result.failure(t))
                }
            })
        }
    }
}