package net.artux.pda.repositories

import net.artux.pda.model.Summary
import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class QuestRepository @Inject constructor(
    private val defaultApi: DefaultApi,
    private val storyDataCache: Cache<StoryData>,
    private val storyCache: Cache<StoryDto>,
    private val summaryCache: Cache<Summary>,
    private val questCache: Cache<StoryDto>
) {

    fun clearCache() {
        storyDataCache.clear()
        questCache.clear()
        storyCache.clear()
        summaryCache.clear()
    }

    fun getCachedStoryData(): Result<StoryData> {
        val cache = storyDataCache.get("story")
        return if (cache != null)
            Result.success(cache)
        else Result.failure(java.lang.Exception("Cache isn't found"))
    }

    fun getCachedStory(storyId: Int): Result<StoryDto> {
        val cache = questCache.get("$storyId")
        return if (cache != null)
            Result.success(cache)
        else Result.failure(java.lang.Exception("Cache isn't found"))
    }

    suspend fun updateStories(): Result<List<StoryInfo>> {
        return suspendCoroutine {
            defaultApi.stories.enqueue(object : Callback<List<StoryInfo>> {
                override fun onResponse(
                    call: Call<List<StoryInfo>>,
                    response: Response<List<StoryInfo>>
                ) {
                    val data = response.body()
                    if (data != null) {
                        for (story in data) {
                            //storyCache.put(story.id.toString(), story)//TODO
                        }
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Chapter null: $response")))
                }

                override fun onFailure(call: Call<List<StoryInfo>>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun getChapter(storyId: Int, chapterId: Int): Result<ChapterDto> {
        val story = getStory(storyId)
        story.onSuccess {
            val chapter = it.chapters[chapterId.toString()]
            return if (chapter != null) Result.success(chapter)
            else Result.failure(Exception("Chapter not found!"))
        }
        return Result.failure(Exception("Chapter not found in cached story"))
    }

    private suspend fun getStory(storyId: Int): Result<StoryDto> {
        return suspendCoroutine {
            val story = questCache.get(storyId.toString())
            if (story != null)
                it.resume(Result.success(story))
            else {
                defaultApi.getStory(storyId.toLong())
                    .enqueue(object : Callback<StoryDto> {
                        override fun onResponse(call: Call<StoryDto>, response: Response<StoryDto>) {
                            val data = response.body()
                            if (data != null) {
                                questCache.put("$storyId", data)
                                it.resume(Result.success(data))
                            } else
                                it.resume(Result.failure(Exception("Story pull error: $response")))
                        }

                        override fun onFailure(call: Call<StoryDto>, t: Throwable) {
                            t.printStackTrace()
                            it.resume(Result.failure(java.lang.Exception(t)))
                        }
                    })
            }
        }
    }

    suspend fun getMap(storyId: Int, mapId: Int): Result<GameMap> {
        val story = getStory(storyId)
        story.onSuccess {
            val map = it.maps[mapId.toString()]
            if (map != null)
                return Result.success(map)
            return Result.failure(Exception("Map not found!"))
        }
        return Result.failure(Exception("Map not found in cached story"))
    }


    suspend fun resetData(): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.resetData().enqueue(object : Callback<StoryData> {
                override fun onResponse(
                    call: Call<StoryData>,
                    response: Response<StoryData>
                ) {
                    val data = response.body()
                    if (data != null) {
                        storyDataCache.put("story", data)
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception(response.toString())))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun setWearableItem(id: UUID): Result<Status> {
        return suspendCoroutine {
            defaultApi.setItem(id).enqueue(object : Callback<Status> {
                override fun onResponse(
                    call: Call<Status>,
                    response: Response<Status>
                ) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Status null")))
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun syncMember(map: CommandBlock): Result<StoryData> {
        return suspendCoroutine {
            Timber.tag("Quest Repository").d("Syncing, actions: ${map.actions}")
            defaultApi.applyCommands(map).enqueue(object : Callback<StoryData> {
                override fun onResponse(
                    call: Call<StoryData>,
                    response: Response<StoryData>
                ) {
                    val data = response.body()
                    if (data != null) {
                        storyDataCache.put("story", data)
                        Timber.d("Repository got storyData: $data")
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception(response.toString())))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }


    suspend fun getStoryData(): Result<StoryData> {
        return suspendCoroutine {
            defaultApi.currentStoryData.enqueue(object : Callback<StoryData> {
                override fun onResponse(call: Call<StoryData>, response: Response<StoryData>) {
                    val data = response.body()
                    if (data != null) {
                        storyDataCache.put("story", data)
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Story data null")))
                }

                override fun onFailure(call: Call<StoryData>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }

}