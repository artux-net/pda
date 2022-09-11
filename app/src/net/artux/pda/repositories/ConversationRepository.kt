package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.ConversationCreateDTO
import net.artux.pdanetwork.model.ConversationDTO
import net.artux.pdanetwork.model.QueryPage
import net.artux.pdanetwork.model.SliceConversationDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ConversationRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val cache: Cache<ConversationDTO>,
) {

    fun clearCache() {
        cache.clear()
    }

    fun getCachedConversations(): Result<List<ConversationDTO>> {
        return if (cache.all != null)
            Result.success(cache.all)
        else Result.failure(Exception("Note isn't found"))
    }

    suspend fun getConversations(queryPage: QueryPage): Result<List<ConversationDTO>> {
        return suspendCoroutine {
            webservice.getConversations(queryPage).enqueue(object : Callback<SliceConversationDTO> {
                override fun onResponse(
                    call: Call<SliceConversationDTO>,
                    response: Response<SliceConversationDTO>
                ) {
                    val data = response.body()
                    if (data != null) {
                        clearCache()
                        for (conversation in data.content) {
                            cache.put(conversation.id.toString(), conversation)
                        }
                        it.resume(Result.success(data.content))
                    } else
                        it.resume(Result.failure(Exception(response.toString())))
                }

                override fun onFailure(call: Call<SliceConversationDTO>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }

            })
        }
    }

    suspend fun createConversation(conversationCreateDTO: ConversationCreateDTO): Result<ConversationDTO> {
        return suspendCoroutine {
            webservice.createConversation(conversationCreateDTO)
                .enqueue(object : Callback<ConversationDTO> {
                    override fun onResponse(
                        call: Call<ConversationDTO>,
                        response: Response<ConversationDTO>
                    ) {
                        val data = response.body()
                        if (data != null) {
                            cache.put(data.id.toString(), data)
                            it.resume(Result.success(data))
                        } else
                            it.resume(Result.failure(Exception(response.toString())))
                    }

                    override fun onFailure(call: Call<ConversationDTO>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }
                })
        }
    }

}