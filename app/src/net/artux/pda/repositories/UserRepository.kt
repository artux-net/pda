package net.artux.pda.repositories

import net.artux.pda.common.PropertyFields
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.user.UserRelation
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
class UserRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val userCache: Cache<Profile>,
    private val dataCache: Cache<StoryData>,
    private val memberCache: Cache<UserDto>
) {

    fun clearMemberCache() {
        memberCache.clear()
    }

    fun getCachedProfile(userId: UUID): Result<Profile> {
        val cache = userCache.get(userId.toString())
        return if (cache != null)
            Result.success(cache)
        else Result.failure(Exception("Cache isn't found"))
    }

    fun getCachedData(): Result<StoryData> {
        val cache = dataCache.get("story")
        return if (cache != null)
            Result.success(cache)
        else Result.failure(Exception("Cache isn't found"))
    }

    suspend fun getProfile(userId: UUID): Result<Profile> {
        return suspendCoroutine {
            webservice.getProfile1(userId).enqueue(object : Callback<Profile> {
                override fun onResponse(
                    call: Call<Profile>,
                    response: Response<Profile>
                ) {
                    val data = response.body()
                    if (data != null) {
                        userCache.put(userId.toString(), data)
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }

            })
        }
    }

    suspend fun resetPassword(email: String): Result<Status> {
        return suspendCoroutine {
            webservice.sendLetter(email).enqueue(object : Callback<Status> {
                override fun onResponse(
                    call: Call<Status>,
                    response: Response<Status>
                ) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }

            })
        }
    }

    fun getCachedMember(): Result<UserDto> {
        val cache = memberCache.get("user")
        return if (cache != null)
            Result.success(cache)
        else Result.failure(java.lang.Exception("Cache isn't found"))
    }

    fun isUserTester():Boolean{
        return getCachedMember().map { it.role }.map {
            it != UserDto.RoleEnum.USER
        }.getOrElse { false }
    }

    suspend fun getMember(): Result<UserDto> {
        return suspendCoroutine {
            Timber.i("Request server for userDto")
            webservice.loginUser().enqueue(object : Callback<UserDto> {
                override fun onResponse(
                    call: Call<UserDto>,
                    response: Response<UserDto>
                ) {
                    val data = response.body()
                    Timber.i("Got response")
                    if (data != null) {
                        memberCache.put("user", data)

                        it.resume(Result.success(data))
                    } else {
                        val error = response.toString()
                        Timber.i(error)
                        it.resume(Result.failure(Exception(error)))
                    }

                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    Timber.e("Got error")
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }


    suspend fun registerUser(registerUser: RegisterUserDto): Result<Status> {
        return suspendCoroutine {
            webservice.registerUser(registerUser).enqueue(object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                    val data = response.body()
                    if (data != null) {
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception(response.toString())))
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun getRatingPage(numberPage: Int): Result<List<SimpleUserDto>> {
        return suspendCoroutine {
            webservice.getRating(numberPage, 20, "DESC", "xp")
                .enqueue(object : Callback<ResponsePageSimpleUserDto> {
                    override fun onResponse(
                        call: Call<ResponsePageSimpleUserDto>,
                        response: Response<ResponsePageSimpleUserDto>
                    ) {
                        val data = response.body()

                        if (data?.content != null) {
                            it.resume(Result.success(data.content))
                        } else
                            it.resume(Result.failure(Exception("Не удалось загрузить больше")))
                    }

                    override fun onFailure(call: Call<ResponsePageSimpleUserDto>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }
                })
        }
    }

    suspend fun getFriends(uuid: UUID, userRelation: UserRelation): Result<List<SimpleUserDto>> {
        return suspendCoroutine {
            webservice.getFriends(uuid, userRelation.name)
                .enqueue(object : Callback<List<SimpleUserDto>> {
                    override fun onResponse(
                        call: Call<List<SimpleUserDto>>,
                        response: Response<List<SimpleUserDto>>
                    ) {
                        val data = response.body()

                        if (data != null) {
                            it.resume(Result.success(data))
                        } else
                            it.resume(Result.failure(Exception("Не удалось загрузить")))
                    }

                    override fun onFailure(call: Call<List<SimpleUserDto>>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }
                })
        }
    }

    suspend fun getUserRequests(): Result<List<SimpleUserDto>> {
        return suspendCoroutine {
            webservice.friendsRequests
                .enqueue(object : Callback<List<SimpleUserDto>> {
                    override fun onResponse(
                        call: Call<List<SimpleUserDto>>,
                        response: Response<List<SimpleUserDto>>
                    ) {
                        val data = response.body()

                        if (data != null) {
                            it.resume(Result.success(data))
                        } else
                            it.resume(Result.failure(Exception("Не удалось загрузить")))
                    }

                    override fun onFailure(call: Call<List<SimpleUserDto>>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }
                })
        }
    }

}