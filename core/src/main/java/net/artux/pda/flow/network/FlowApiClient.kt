package net.artux.pda.flow.network

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.artux.pda.flow.network.dto.CommandBlockDto
import net.artux.pda.flow.network.dto.GameMapDto
import net.artux.pda.flow.network.dto.RegisterRequestDto
import net.artux.pda.flow.network.dto.StoryDataDto
import net.artux.pda.flow.network.dto.StoryDto
import net.artux.pda.flow.network.dto.StoryInfoDto
import net.artux.pda.flow.network.dto.StatusDto
import net.artux.pda.flow.network.dto.UserInfoDto
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Plain OkHttp + Gson client for the subset of the pdanetwork REST API the iOS registration/
 * login/story/stage flow needs (see the plan for the full endpoint table). Deliberately not
 * Retrofit: Retrofit builds its API implementation via java.lang.reflect.Proxy at runtime,
 * which RoboVM's AOT compiler doesn't handle without extra reflection config - this avoids
 * that risk entirely by making the HTTP calls directly.
 *
 * Auth is HTTP Basic (see app's DataManager.getAuthToken() - Credentials.basic(login, pass)),
 * not a token: there's no separate login endpoint, "logging in" just means these credentials
 * are attached to every subsequent request and a request succeeding (not 401) is the proof.
 */
class FlowApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    // Platform TLS override - iOS passes one that enables SNI (see ios' SniSSLSocketFactory);
    // null keeps OkHttp's default, which is what the JVM tests use.
    sslSocketFactory: SSLSocketFactory? = null,
    trustManager: X509TrustManager? = null
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://dev.artux.net/pdanetwork/"

        /** Where stage backgrounds and other story resources live - Android's RESOURCE_URL. */
        const val RESOURCE_URL = "https://cdn.artux.net/static/"

        /** Same resolution as Android's URLHelper.getResourceURL(): relative paths are CDN-based. */
        fun resourceUrl(path: String): String =
            if (path.contains("http")) path else RESOURCE_URL + path.trimStart('/')
    }

    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .apply {
            if (sslSocketFactory != null && trustManager != null) {
                sslSocketFactory(sslSocketFactory, trustManager)
            }
        }
        .build()

    private fun url(path: String) = baseUrl.trimEnd('/') + "/" + path.trimStart('/')

    private fun Request.Builder.auth(email: String, password: String): Request.Builder =
        addHeader("Authorization", Credentials.basic(email, password))

    // Takes a java.lang.reflect.Type (Class<T> already implements Type, so plain Foo::class.java
    // callers are unaffected) rather than Class<T> - a raw Class can't carry a generic element
    // type, so gson.fromJson(json, List::class.java) would silently deserialize into
    // List<LinkedTreeMap> instead of List<StoryInfoDto>. Callers needing a generic type pass a
    // TypeToken's .type instead (see getStories()).
    private suspend fun <T> execute(request: Request, responseType: java.lang.reflect.Type): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            IOException("HTTP ${response.code}: $bodyString")
                        )
                    }
                    val parsed: T = if (responseType == Unit::class.java) {
                        @Suppress("UNCHECKED_CAST")
                        Unit as T
                    } else {
                        gson.fromJson(bodyString, responseType)
                    }
                    Result.success(parsed)
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: JsonParseException) {
                // Not an IOException - uncaught, it killed the caller's coroutine before it could
                // post a result back, leaving the screen stuck on its loading text.
                Result.failure(e)
            }
        }

    /** Raw bytes of a public resource (stage backgrounds on the CDN) - no auth. */
    suspend fun download(url: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Result.failure(IOException("HTTP ${response.code} for $url"))
                } else {
                    Result.success(response.body?.bytes() ?: ByteArray(0))
                }
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun register(nickname: String, email: String, password: String): Result<Unit> {
        val body = RegisterRequestDto(email, password, nickname, avatar = "0")
        val request = Request.Builder()
            .url(url("api/v1/user/register"))
            .post(gson.toJson(body).toRequestBody(jsonMediaType))
            .build()
        // The server answers registration failures (e.g. a nickname with digits/symbols it
        // rejects) with HTTP 200 and success:false in the body, not a 4xx - execute()'s
        // isSuccessful check alone would treat that as success, so this has to inspect the
        // body explicitly. Confirmed against the real dev backend.
        return execute<StatusDto>(request, StatusDto::class.java).mapCatching { status ->
            if (status.success != true) {
                throw IOException(status.description ?: "Registration failed")
            }
        }
    }

    /**
     * DELETE api/v1/user/delete - not used by any screen in the flow, only by
     * FlowApiClientTest to clean up the throwaway account it registers, same as
     * maestro/scripts/run_register_to_prologue.sh does for its own test accounts.
     */
    suspend fun deleteAccount(email: String, password: String): Result<Unit> {
        val request = Request.Builder()
            .url(url("api/v1/user/delete"))
            .auth(email, password)
            .delete()
            .build()
        return execute(request, Unit::class.java)
    }

    /** Verifies credentials are valid by calling an authenticated endpoint with them. */
    suspend fun checkLogin(email: String, password: String): Result<UserInfoDto> {
        val request = Request.Builder()
            .url(url("api/v1/user/info"))
            .auth(email, password)
            .get()
            .build()
        return execute(request, UserInfoDto::class.java)
    }

    suspend fun getStories(email: String, password: String): Result<List<StoryInfoDto>> {
        val request = Request.Builder()
            .url(url("api/v1/quest"))
            .auth(email, password)
            .get()
            .build()
        val listType = object : TypeToken<List<StoryInfoDto>>() {}.type
        return execute(request, listType)
    }

    suspend fun getStory(storyId: Long, email: String, password: String): Result<StoryDto> {
        val request = Request.Builder()
            .url(url("api/v1/quest/$storyId"))
            .auth(email, password)
            .get()
            .build()
        return execute(request, StoryDto::class.java)
    }

    suspend fun getStoryData(email: String, password: String): Result<StoryDataDto> {
        val request = Request.Builder()
            .url(url("api/v1/user/quest/info"))
            .auth(email, password)
            .get()
            .build()
        return execute(request, StoryDataDto::class.java)
    }

    suspend fun getMap(storyId: Long, mapId: Long, email: String, password: String): Result<GameMapDto> {
        val request = Request.Builder()
            .url(url("api/v1/quest/maps/$storyId/$mapId"))
            .auth(email, password)
            .get()
            .build()
        return execute(request, GameMapDto::class.java)
    }

    /**
     * Records [stageId] as the player's current position - the same "state" command Android's
     * CommandController.checkStage() queues on every stage entry. The server applies that
     * stage's own actions itself when it receives this (confirmed against the dev backend), so
     * callers must not also send stage.actions, or they'd be applied twice.
     */
    suspend fun enterStage(
        storyId: Long,
        chapterId: Long,
        stageId: Long,
        email: String,
        password: String
    ): Result<StoryDataDto> =
        applyCommands(mapOf("state" to listOf("$storyId:$chapterId:$stageId")), email, password)

    suspend fun applyCommands(
        actions: Map<String, List<String>>,
        email: String,
        password: String
    ): Result<StoryDataDto> {
        val body = CommandBlockDto(actions)
        val request = Request.Builder()
            .url(url("api/v1/quest/commands"))
            .auth(email, password)
            .put(gson.toJson(body).toRequestBody(jsonMediaType))
            .build()
        return execute(request, StoryDataDto::class.java)
    }
}
