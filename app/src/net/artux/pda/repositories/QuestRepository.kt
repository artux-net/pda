package net.artux.pda.repositories

import net.artux.pda.generated.apis.DefaultApi
import net.artux.pda.generated.models.StoryData
import net.artux.pda.map.model.input.Map
import net.artux.pda.repositories.util.Result
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
    private val defaultApi: DefaultApi,
    private val storyCache: Cache<StoryData>,
    private val questCache: Cache<Chapter>,
    private val mapCache: Cache<Map>) {

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

    suspend fun getMap(storyId: Int, mapId: Int): Result<Map> {
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


    suspend fun resetData(): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.resetDataUsingGET().enqueue(object : Callback<StoryData> {
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

    suspend fun syncMember(map: HashMap<String, List<String>>): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.doActionsUsingPUT(map).enqueue(object : Callback<StoryData> {
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
            defaultApi.getActualDataUsingGET().enqueue(object : Callback<StoryData>{
                override fun onResponse(call: Call<StoryData>, response: Response<StoryData>) {
                    val data = response.body()
                    if (data!=null) {
                        storyCache.put("story", data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Story null")))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

}