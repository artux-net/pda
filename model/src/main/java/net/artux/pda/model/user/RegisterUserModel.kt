package net.artux.pda.model.user

data class RegisterUserModel(
    var login: String,
    var name: String,
    var nickname: String,
    var email: String,
    var password: String,
    var avatar: String
)