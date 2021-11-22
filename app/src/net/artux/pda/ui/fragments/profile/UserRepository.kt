package net.artux.pda.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import net.artux.pda.PdaAPI
import net.artux.pda.utils.Cache
import net.artux.pdalib.Profile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val webservice: PdaAPI,
    // Простой кэш в памяти. Детали опущены для краткости.
    private val userCache: Cache<Profile>
) {
    fun getProfile(userId: Int): MutableLiveData<Profile> {
        val data: MutableLiveData<Profile> = MutableLiveData<Profile>();
        val cached = userCache.get(userId.toString())
        if (cached != null) {
            data.value = cached
            return data
        }
        userCache.put(userId.toString(), data)
        // Эта реализация все еще неоптимальная, но лучше, чем раньше.
        // Полная реализация также обрабатывает случаи ошибок.
        webservice.getProfile(userId).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                data.value = response.body()
            }

            // Случай ошибки опущен для краткости.
            override fun onFailure(call: Call<Profile>, t: Throwable) {
                TODO()
            }
        })
        return data
    }

    fun getProfile(userId: Int, data: MutableLiveData<Profile>) {
        val cached = userCache.get(userId.toString())
        if (cached != null) {
            data.value = cached
        }
        userCache.put(userId.toString(), data)
        // Эта реализация все еще неоптимальная, но лучше, чем раньше.
        // Полная реализация также обрабатывает случаи ошибок.
        webservice.getProfile(userId).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                data.postValue(response.body())
            }

            // Случай ошибки опущен для краткости.
            override fun onFailure(call: Call<Profile>, t: Throwable) {
                TODO()
            }
        })
    }

}