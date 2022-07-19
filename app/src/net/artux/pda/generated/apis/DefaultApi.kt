package net.artux.pda.generated.apis

import net.artux.pda.generated.models.*
import retrofit2.Call
import retrofit2.http.*

interface DefaultApi {
    /**
     * Операция с предметом
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param action action
     * @param seller seller (optional)
     * @return [Call]<[Status]>
     */
    @POST("pdanetwork/items/{action}")
    fun actionWithItemUsingPOST(@Path("action") action: kotlin.String, @Body seller: kotlin.Int? = null): Call<Status>

    /**
     * Запросить/добавить/удалить друга
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param pdaId pdaId
     * @return [Call]<[Status]>
     */
    @POST("pdanetwork/profile/friends")
    fun addFriendUsingPOST(@Query("pdaId") pdaId: kotlin.Long): Call<Status>

    /**
     * Добавить предмет (только для админа)
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param type type (optional)
     * @return [Call]<[Status]>
     */
    @POST("pdanetwork/items/add")
    fun addItemUsingPOST(@Body type: kotlin.Int? = null): Call<Status>

    /**
     * Подтверждение регистрации пользователя
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param token token (optional)
     * @return [Call]<[kotlin.String]>
     */
    @GET("pdanetwork/register")
    fun confirmRegistrationUsingGET(@Query("token") token: kotlin.String? = null): Call<kotlin.String>

    /**
     * createNote
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param title title
     * @return [Call]<[NoteDto]>
     */
    @POST("pdanetwork/notes")
    fun createNoteUsingPOST(@Body title: kotlin.String): Call<NoteDto>

    /**
     * deleteNote
     * 
     * Responses:
     *  - 200: OK
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param id id
     * @return [Call]<[Status]>
     */
    @DELETE("pdanetwork/notes")
    fun deleteNoteUsingDELETE(@Query("id") id: kotlin.Long): Call<Status>

    /**
     * Выполнение
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param actions actions
     * @return [Call]<[StoryData]>
     */
    @PUT("pdanetwork/commands/do")
    @JvmSuppressWildcards
    fun doActionsUsingPUT(@Body actions: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>): Call<StoryData>

    /**
     * editNote
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param noteEntity noteEntity
     * @return [Call]<[NoteDto]>
     */
    @PUT("pdanetwork/notes")
    fun editNoteUsingPUT(@Body noteEntity: NoteDto): Call<NoteDto>

    /**
     * Редактирование информации
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param user user
     * @return [Call]<[Status]>
     */
    @PUT("pdanetwork/edit")
    fun editUserUsingPUT(@Body user: RegisterUserDto): Call<Status>

    /**
     * getAchievements
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[kotlin.collections.List<AchievementEntity>]>
     */
    @GET("pdanetwork/profile/achievements")
    fun getAchievementsUsingGET(): Call<kotlin.collections.List<AchievementEntity>>

    /**
     * getAchievements
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param id id
     * @return [Call]<[kotlin.collections.List<AchievementEntity>]>
     */
    @GET("pdanetwork/profile/achievements/{id}")
    fun getAchievementsUsingGET1(@Path("id") id: kotlin.Long): Call<kotlin.collections.List<AchievementEntity>>

    /**
     * Информация о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[StoryData]>
     */
    @GET("pdanetwork/commands")
    fun getActualDataUsingGET(): Call<StoryData>

    /**
     * Получить запросы дружбы
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[kotlin.collections.List<FriendModel>]>
     */
    @GET("pdanetwork/profile/friends/requests")
    fun getFriendsRequestsUsingGET(): Call<kotlin.collections.List<FriendModel>>

    /**
     * Получить друзей
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[kotlin.collections.List<FriendModel>]>
     */
    @GET("pdanetwork/profile/friends")
    fun getFriendsUsingGET(): Call<kotlin.collections.List<FriendModel>>

    /**
     * Получить друзей по pdaId
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param id id
     * @return [Call]<[kotlin.collections.List<FriendModel>]>
     */
    @GET("pdanetwork/profile/friends/{id}")
    fun getFriendsUsingGET1(@Path("id") id: kotlin.Long): Call<kotlin.collections.List<FriendModel>>

    /**
     * getNotes
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[kotlin.collections.List<NoteDto>]>
     */
    @GET("pdanetwork/notes")
    fun getNotesUsingGET(): Call<kotlin.collections.List<NoteDto>>

    /**
     * getProfile
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[Profile]>
     */
    @GET("pdanetwork/profile")
    fun getProfileUsingGET(): Call<Profile>

    /**
     * getProfile
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param id id
     * @return [Call]<[Profile]>
     */
    @GET("pdanetwork/profile/{id}")
    fun getProfileUsingGET1(@Path("id") id: kotlin.Long): Call<Profile>

    /**
     * Рейтинг пользователей
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param number  (optional)
     * @param size  (optional)
     * @param sortBy  (optional)
     * @param sortDirection  (optional)
     * @return [Call]<[ResponsePageUserInfoDto]>
     */
    @GET("pdanetwork/profile/rating")
    fun getRatingUsingGET(@Query("number") number: kotlin.Int? = null, @Query("size") size: kotlin.Int? = null, @Query("sortBy") sortBy: kotlin.String? = null, @Query("sortDirection") sortDirection: kotlin.String? = null): Call<ResponsePageUserInfoDto>

    /**
     * Получить продавца
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param id id
     * @return [Call]<[SellerDto]>
     */
    @GET("pdanetwork/items/{id}")
    fun getSellerUsingGET(@Path("id") id: kotlin.Int): Call<SellerDto>

    /**
     * Получить подписчиков
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[kotlin.collections.List<FriendModel>]>
     */
    @GET("pdanetwork/profile/friends/subs")
    fun getSubsUsingGET(): Call<kotlin.collections.List<FriendModel>>

    /**
     * Получить подписчиков по pdaId
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param id id
     * @return [Call]<[kotlin.collections.List<FriendModel>]>
     */
    @GET("pdanetwork/profile/friends/subs/{id}")
    fun getSubsUsingGET1(@Path("id") id: kotlin.Long): Call<kotlin.collections.List<FriendModel>>

    /**
     * Пользователь
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[UserDto]>
     */
    @GET("pdanetwork/info")
    fun loginUserUsingGET(): Call<UserDto>

    /**
     * Регистрация пользователя
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param registerUser registerUser
     * @return [Call]<[Status]>
     */
    @POST("pdanetwork/register")
    fun registerUserUsingPOST(@Body registerUser: RegisterUserDto): Call<Status>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [Call]<[UserDto]>
     */
    @DELETE("pdanetwork/reset/data")
    fun resetDataUsingDELETE(): Call<UserDto>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[UserDto]>
     */
    @GET("pdanetwork/reset/data")
    fun resetDataUsingGET(): Call<StoryData>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [Call]<[UserDto]>
     */
    @HEAD("pdanetwork/reset/data")
    fun resetDataUsingHEAD(): Call<UserDto>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [Call]<[UserDto]>
     */
    @OPTIONS("pdanetwork/reset/data")
    fun resetDataUsingOPTIONS(): Call<UserDto>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [Call]<[UserDto]>
     */
    @PATCH("pdanetwork/reset/data")
    fun resetDataUsingPATCH(): Call<UserDto>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[UserDto]>
     */
    @POST("pdanetwork/reset/data")
    fun resetDataUsingPOST(): Call<UserDto>

    /**
     * Сброс информации о прохождении
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @return [Call]<[UserDto]>
     */
    @PUT("pdanetwork/reset/data")
    fun resetDataUsingPUT(): Call<UserDto>

    /**
     * Запрос сброса пароля
     * 
     * Responses:
     *  - 200: OK
     *  - 201: Created
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param email email
     * @return [Call]<[Status]>
     */
    @PUT("pdanetwork/reset")
    fun sendLetterUsingPUT(@Query("email") email: kotlin.String): Call<Status>

}