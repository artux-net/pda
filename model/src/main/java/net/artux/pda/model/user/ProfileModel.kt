package net.artux.pda.model.user

import java.time.Instant
import java.util.UUID

class ProfileModel {
    var id: UUID? = null
    var login: String? = null
    var name: String? = null
    var nickname: String? = null
    var avatar: String? = null
    var pdaId = 0
    var xp = 0
    var registration: Instant? = null
    var friendRelation: FriendRelation? = null
    var gang: Gang? = null
    var relations: GangRelation? = null
    var achievements = 0
    var friendStatus = 0

    /*
    0 - is not friend
    1 - friend
    2 - subscriber
    3 - requested
     */
    var friends = 0
    var subs = 0

    constructor() {}
    constructor(userModel: UserModel) {
        login = userModel.login
        name = userModel.name
        avatar = userModel.avatar
        pdaId = Math.toIntExact(userModel.pdaId!!)
        xp = userModel.xp
        registration = userModel.registration
    }
}