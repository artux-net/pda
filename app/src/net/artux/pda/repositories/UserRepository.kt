package net.artux.pda.repositories

import net.artux.pda.services.PdaAPI
import net.artux.pdalib.Member
import net.artux.pdalib.Profile
import net.artux.pdalib.Status
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepository @Inject constructor(
    private val webservice: PdaAPI,
    private val userCache: Cache<Profile>,
    private val memberCache: Cache<Member>) {

    fun clearMemberCache() {
        memberCache.clear()
    }

    fun getCachedProfile(userId: Int): Result<Profile> {
        val cache = userCache.get(userId.toString())
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))
    }

    suspend fun getProfile(userId: Int): Result<Profile>{
        return suspendCoroutine {
            webservice.getProfile(userId).enqueue(object : Callback<Profile> {
                override fun onResponse(
                    call: Call<Profile>,
                    response: retrofit2.Response<Profile>
                ) {
                    val data = response.body()
                    if (data!=null) {
                        userCache.put(userId.toString(), data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }

            })
        }
    }

    fun getCachedMember(): Result<Member> {
        val cache = memberCache.get("user")
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(java.lang.Exception("Cache isn't found"))

    }

    suspend fun getMember(): Result<Member>{
        return suspendCoroutine {
            webservice.loginUser().enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: retrofit2.Response<Member>) {
                    val data = response.body()
                    if (data!=null) {
                        memberCache.put("user", data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun syncMember(map: HashMap<String, List<String>>): Result<Member>{
        return suspendCoroutine {
            webservice.synchronize(map).enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: retrofit2.Response<Member>) {
                    val data = response.body()
                    if (data!=null) {
                        memberCache.put("user", data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun resetData(): Result<Member>{
        return suspendCoroutine {
            webservice.resetData().enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: retrofit2.Response<Member>) {
                    val data = response.body()
                    if (data!=null) {
                        memberCache.put("user", data)
                        it.resume(Result.Success(data))
                    }else
                        it.resume(Result.Error(Exception("Status null")))
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

}