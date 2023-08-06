package net.artux.pda.repositories

import net.artux.pda.model.Summary
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.story.StoryStateModel
import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.ChapterDto
import net.artux.pdanetwork.model.CommandBlock
import net.artux.pdanetwork.model.GameMap
import net.artux.pdanetwork.model.Status
import net.artux.pdanetwork.model.StoryData
import net.artux.pdanetwork.model.StoryDto
import net.artux.pdanetwork.model.StoryInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.UUID
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
    private val questCache: Cache<StoryDto>,
    private val storyMapper: StoryMapper
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

    fun getCurrentState(): StoryStateModel? {
        return getCachedStoryData()
            .map { storyMapper.dataModel(it).currentState }
            .getOrNull()
    }

    fun getCurrentStoryId(): Int {
        return getCurrentState()?.storyId ?: -1
    }

    fun getCurrentChapterId(): Int {
        return getCurrentState()?.chapterId ?: -1
    }

    suspend fun updateStories(): Result<List<StoryInfo>> {
        Timber.i("Fetch stories info from server")
        return suspendCoroutine {
            defaultApi.stories.enqueue(object : Callback<List<StoryInfo>> {
                override fun onResponse(
                    call: Call<List<StoryInfo>>,
                    response: Response<List<StoryInfo>>
                ) {
                    val data = response.body()
                    if (data != null) {
                        Timber.i("${data.size} stories fetched from server")
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Не удалось обновить сюжеты с сервера")))
                }

                override fun onFailure(call: Call<List<StoryInfo>>, t: Throwable) {
                    Timber.e(t.message)
                    it.resume(Result.failure(Exception("Не удалось обновить сюжеты с сервера")))
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
        return Result.failure(Exception("Chapter $chapterId not found in cached story $storyId"))
    }

    private suspend fun getStory(storyId: Int): Result<StoryDto> {
        return suspendCoroutine {
            val story = questCache.get(storyId.toString())
            if (story != null)
                it.resume(Result.success(story))
            else {
                defaultApi.getStory(storyId.toLong())
                    .enqueue(object : Callback<StoryDto> {
                        override fun onResponse(
                            call: Call<StoryDto>,
                            response: Response<StoryDto>
                        ) {
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
            Timber.tag("Quest Repository").d("Синхронизация, команды: ${map.actions}")
            defaultApi.applyCommands(map).enqueue(object : Callback<StoryData> {
                override fun onResponse(
                    call: Call<StoryData>,
                    response: Response<StoryData>
                ) {
                    val data = response.body()
                    if (data != null) {
                        storyDataCache.put("story", data)
                        Timber.i("Синхронизация прошла успешно, ответ: $data")
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