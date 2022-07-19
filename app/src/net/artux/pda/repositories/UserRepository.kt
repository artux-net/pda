package net.artux.pda.repositories

import net.artux.pda.generated.apis.DefaultApi
import net.artux.pda.generated.models.*
import net.artux.pda.repositories.util.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    fun getCachedProfile(userId: Int): Result<Profile> {
        val cache = userCache.get(userId.toString())
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(Exception("Cache isn't found"))
    }

    suspend fun getProfile(userId: Long): Result<Profile> {
        return suspendCoroutine {
            webservice.getProfileUsingGET1(userId).enqueue(object : Callback<Profile> {
                override fun onResponse(
                    call: Call<Profile>,
                    response: retrofit2.Response<Profile>
                ) {
                    val data = response.body()
                    if (data != null) {
                        userCache.put(userId.toString(), data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }

            })
        }
    }

    fun getCachedMember(): Result<UserDto> {
        val cache = memberCache.get("user")
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))

    }

    suspend fun getMember(): Result<UserDto> {
        return suspendCoroutine {
            webservice.loginUserUsingGET().enqueue(object : Callback<UserDto> {
                override fun onResponse(
                    call: Call<UserDto>,
                    response: retrofit2.Response<UserDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        memberCache.put("user", data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }





    suspend fun registerUser(registerUser: RegisterUserDto): Result<Status> {
        return suspendCoroutine {
            webservice.registerUserUsingPOST(registerUser).enqueue(object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
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

    suspend fun getRatingPage(numberPage: Int): Result<List<UserInfoDto>> {
        return suspendCoroutine {
            webservice.getRatingUsingGET(numberPage).enqueue(object : Callback<ResponsePageUserInfoDto> {
                override fun onResponse(call: Call<ResponsePageUserInfoDto>, response: Response<ResponsePageUserInfoDto>) {
                    val data = response.body()

                    if (data?.data != null) {
                        it.resume(Result.Success(data.data))
                    } else
                        it.resume(Result.Error(Exception("Не удалось загрузить больше")))
                }

                override fun onFailure(call: Call<ResponsePageUserInfoDto>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

}