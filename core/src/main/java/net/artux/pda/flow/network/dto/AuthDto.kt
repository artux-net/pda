package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Field names mirror net.artux.pdanetwork.model.RegisterUserDto (app/build/generated
// swagger-code) so Gson maps the same wire format the real API expects.
data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("avatar") val avatar: String
)

// Mirrors net.artux.pdanetwork.model.UserDto - only the fields the flow actually reads.
data class UserInfoDto(
    @SerializedName("login") val login: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("pdaId") val pdaId: Int? = null
)

// Mirrors net.artux.pdanetwork.model.Status - the backend's response envelope for
// register() (and others). Unlike most other endpoints, a failure here still comes back as
// HTTP 200 with success:false in the body (confirmed against the real dev backend - e.g. a
// nickname with digits in it gets rejected this way, not via a 4xx), so callers need to check
// this field explicitly rather than trusting the HTTP status alone.
data class StatusDto(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("code") val code: Int? = null,
    @SerializedName("description") val description: String? = null
)
