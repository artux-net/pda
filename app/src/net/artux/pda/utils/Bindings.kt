package net.artux.pda.utils

import android.annotation.SuppressLint
import net.artux.pda.R
import net.artux.pda.databinding.ItemChatBinding
import net.artux.pda.model.user.SimpleUserModel
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper.setAvatar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@SuppressLint("SetTextI18n")
inline fun init(
    binding: ItemChatBinding,
    simpleUserModel: SimpleUserModel,
    // UserMessage.timestamp is epoch millis, not Instant - RoboVM's runtime (used by the
    // iOS build sharing this model) doesn't implement java.time.
    timestampMillis: Long? = null
) {
    val resources = binding.info.context.resources

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    var info =
        if (simpleUserModel.pdaId < 0 || simpleUserModel.gang == null) {
            " [PDA ###]"
        } else {
            val gangId = simpleUserModel.gang!!.id
            val gangTitle = resources.getStringArray(R.array.groups)[gangId]
            " [PDA #" + simpleUserModel.pdaId + "] $gangTitle"
        }
    if (timestampMillis != null)
        info += " - ${formatter.format(Instant.ofEpochMilli(timestampMillis))}"

    binding.info.text = info

    if (simpleUserModel.nickname == null)
        binding.nickname.text = simpleUserModel.name
    else
        binding.nickname.text = "${simpleUserModel.name} ${simpleUserModel.nickname}"

    setAvatar(binding.avatar, simpleUserModel.avatar)
}
