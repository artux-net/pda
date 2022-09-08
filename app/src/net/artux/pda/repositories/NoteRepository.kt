package net.artux.pda.repositories

import net.artux.pda.repositories.util.Result
import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.NoteCreateDto
import net.artux.pdanetwork.model.NoteDto
import net.artux.pdanetwork.model.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class NoteRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val cache: Cache<NoteDto>,
) {

    fun clearCache() {
        cache.clear()
    }

    fun getCachedNotes(): Result<List<NoteDto>> {
        return if (cache.all.size > 0)
            Result.Success(cache.all)
        else Result.Error(Exception("Notes isn't found"))
    }

    fun getCachedNote(id: UUID): Result<NoteDto> {
        val cache = this.cache.get(id.toString())
        return if (cache != null)
            Result.Success(cache)
        else Result.Error(Exception("Note isn't found"))
    }

    suspend fun getNotes(): Result<List<NoteDto>> {
        return suspendCoroutine {
            webservice.notes.enqueue(object : Callback<List<NoteDto>> {
                override fun onResponse(
                    call: Call<List<NoteDto>>,
                    response: Response<List<NoteDto>>
                ) {
                    val data = response.body()
                    if (data != null) {
                        clearCache()
                        for (note in data) {
                            cache.put(note.id.toString(), note)
                        }
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<List<NoteDto>>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }

            })
        }
    }

    suspend fun editNote(noteCreateDto: NoteCreateDto, uuid: UUID): Result<NoteDto> {
        return suspendCoroutine {
            webservice.editNote(noteCreateDto, uuid).enqueue(object : Callback<NoteDto> {
                override fun onResponse(
                    call: Call<NoteDto>,
                    response: Response<NoteDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        cache.put(data.id.toString(), data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Profile null")))
                }

                override fun onFailure(call: Call<NoteDto>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }


    suspend fun createNote(noteCreateDto: NoteCreateDto): Result<NoteDto> {
        return suspendCoroutine {
            webservice.createNote(noteCreateDto).enqueue(object : Callback<NoteDto> {
                override fun onResponse(call: Call<NoteDto>, response: Response<NoteDto>) {
                    val data = response.body()
                    if (data != null) {
                        cache.put(data.id.toString(), data)
                        it.resume(Result.Success(data))
                    } else
                        it.resume(Result.Error(Exception("Status null")))
                }

                override fun onFailure(call: Call<NoteDto>, t: Throwable) {
                    it.resume(Result.Error(java.lang.Exception(t)))
                }
            })
        }
    }

    suspend fun deleteNote(uuid: UUID): Result<Status> {
        return suspendCoroutine {
            webservice.deleteNote(uuid).enqueue(object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                    val data = response.body()
                    if (data != null) {
                        cache.remove(uuid.toString());
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

}