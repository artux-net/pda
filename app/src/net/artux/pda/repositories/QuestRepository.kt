package net.artux.pda.repositories

import net.artux.pda.map.model.Map
import net.artux.pda.services.PdaAPI
import net.artux.pda.ui.fragments.quest.models.Chapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class QuestRepository @Inject constructor(
    private val webservice: PdaAPI,
    private val questCache: Cache<Chapter>,
    private val mapCache: Cache<Map>) {

    fun clearCache() {
        mapCache.clear()
        questCache.clear()
    }

    fun getCachedChapter(storyId: Int, chapterId: Int): Result<Chapter> {
        val cache = questCache.get("$storyId:$chapterId")
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))
    }

    fun getCachedMap(storyId: Int, mapId: Int): Result<Map> {
        val cache = mapCache.get("$storyId:$mapId")
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))
    }

    suspend fun getChapter(storyId: Int, chapterId: Int): Result<Chapter>{
        return suspendCoroutine {
            webservice.getQuest(storyId, chapterId).enqueue(object : Callback<Chapter>{
                override fun onResponse(call: Call<Chapter>, response: Response<Chapter>) {
                    val data = response.body()
                    if (data!=null) {
                        questCache.put(("$storyId:$chapterId").toString(), data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Chapter null")))
                }

                override fun onFailure(call: Call<Chapter>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun getMap(storyId: Int, mapId: Int): Result<Map>{
        return suspendCoroutine {
            webservice.getMap(storyId, mapId).enqueue(object : Callback<Map>{
                override fun onResponse(call: Call<Map>, response: Response<Map>) {
                    val data = response.body()
                    if (data!=null) {
                        mapCache.put(("$storyId:$mapId").toString(), data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Chapter null")))
                }

                override fun onFailure(call: Call<Map>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }
}