package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val userCache: Cache<Profile>,
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

    suspend fun getMember(): Result<UserDto> {
        return suspendCoroutine {
            webservice.loginUser().enqueue(object : Callback<UserDto> {
                override fun onResponse(
                    call: Call<UserDto>,
                    response: Response<UserDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        memberCache.put("user", data)
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Profile null")))
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
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
                        it.resume(Result.failure(Exception("Status null")))
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

                        if (data?.data != null) {
                            it.resume(Result.success(data.data))
                        } else
                            it.resume(Result.failure(Exception("Не удалось загрузить больше")))
                    }

                    override fun onFailure(call: Call<ResponsePageSimpleUserDto>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }
                })
        }
    }

}