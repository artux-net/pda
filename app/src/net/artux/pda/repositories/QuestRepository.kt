package net.artux.pda.repositories

import net.artux.pda.map.model.input.Map
import net.artux.pda.repositories.util.Result
import net.artux.pda.services.PdaAPI
import net.artux.pda.ui.fragments.quest.models.Chapter
import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.CommandBlock
import net.artux.pdanetwork.model.Status
import net.artux.pdanetwork.model.StoryData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class QuestRepository @Inject constructor(
    private val webservice: PdaAPI,
    private val defaultApi: DefaultApi,
    private val storyCache: Cache<StoryData>,
    private val questCache: Cache<Chapter>,
    private val mapCache: Cache<Map>
) {

    fun clearCache() {
        mapCache.clear()
        storyCache.clear()
        questCache.clear()
    }

    fun getCachedStoryData(): Result<StoryData> {
        val cache = storyCache.get("story")
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))
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

    suspend fun getChapter(storyId: Int, chapterId: Int): Result<Chapter> {
        return suspendCoroutine {
            webservice.getQuest(storyId, chapterId).enqueue(object : Callback<Chapter> {
                override fun onResponse(call: Call<Chapter>, response: Response<Chapter>) {
                    val data = response.body()
                    if (data != null) {
                        questCache.put(("$storyId:$chapterId").toString(), data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Chapter null")))
                }

                override fun onFailure(call: Call<Chapter>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun getMap(storyId: Int, mapId: Int): Result<Map> {
        return suspendCoroutine {
            webservice.getMap(storyId, mapId).enqueue(object : Callback<Map> {
                override fun onResponse(call: Call<Map>, response: Response<Map>) {
                    val data = response.body()
                    if (data != null) {
                        mapCache.put(("$storyId:$mapId").toString(), data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Chapter null")))
                }

                override fun onFailure(call: Call<Map>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }


    suspend fun resetData(): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.resetData().enqueue(object : Callback<StoryData> {
                override fun onResponse(
                    call: Call<StoryData>,
                    response: retrofit2.Response<StoryData>
                ) {
                    val data = response.body()
                    if (data != null) {
                        storyCache.put("story", data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Status null")))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun setWearableItem(id: UUID, type: String): Result<Status> {
        return suspendCoroutine {
            defaultApi.setItem(id, type).enqueue(object : Callback<Status> {
                override fun onResponse(
                    call: Call<Status>,
                    response: Response<Status>
                ) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Status null")))
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun syncMember(map: CommandBlock): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.doActions(map).enqueue(object : Callback<StoryData> {
                override fun onResponse(
                    call: Call<StoryData>,
                    response: Response<StoryData>
                ) {
                    val data = response.body()
                    if (data != null) {
                        storyCache.put("story", data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Story null")))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }


    suspend fun getStoryData(): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.actualData.enqueue(object : Callback<StoryData> {
                override fun onResponse(call: Call<StoryData>, response: Response<StoryData>) {
                    val data = response.body()
                    if (data != null) {
                        storyCache.put("story", data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Story null")))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

}