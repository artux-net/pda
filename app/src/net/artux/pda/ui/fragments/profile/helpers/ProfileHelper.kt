package net.artux.pda.ui.fragments.profile.helpers

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import net.artux.pda.R
import net.artux.pda.model.quest.story.StoryDataModel.Companion.getRang
import net.artux.pda.model.user.Gang
import net.artux.pda.model.user.ProfileModel
import net.artux.pda.utils.URLHelper
import java.time.Instant

object ProfileHelper {

    @JvmStatic
    fun getGroup(gang: Gang, context: Context): String {
        return context.resources.getStringArray(R.array.groups)[gang.id]
    }

    @JvmStatic
    fun getGroup(context: Context, group: Int): String {
        return context.resources.getStringArray(R.array.groups)[group]
    }

    private fun isInteger(str: String?): Boolean {
        if (str == null) {
            return false
        }
        val length = str.length
        if (length == 0) {
            return false
        }
        var i = 0
        if (str[0] == '-') {
            if (length == 1) {
                return false
            }
            i = 1
        }
        while (i < length) {
            val c = str[i]
            if (c < '0' || c > '9') {
                return false
            }
            i++
        }
        return true
    }

    @JvmStatic
    fun setGangAvatar(imageView: ImageView, gang: Gang) {
        val context = imageView.context
        Glide.with(context)
            .load(Uri.parse("file:///android_asset/textures/avatars/gangs/g${gang.id}.png"))
            .into(imageView)
    }

    @JvmStatic
    fun setAvatar(imageView: ImageView, avatar: String?) {
        val context = imageView.context
        if (avatar.isNullOrBlank())
            Glide.with(context)
                .asDrawable()
                .load(AppCompatResources.getDrawable(context, R.mipmap.ic_launcher))
                .into(imageView)
        else if (isInteger(avatar))
            Glide.with(context)
                .load(Uri.parse("file:///android_asset/textures/avatars/a${avatar.toInt() + 1}.png"))
                .into(imageView)
        else {
            val netAvatar = URLHelper.getResourceURL(avatar)

            Glide.with(context)
                .asDrawable()
                .load(netAvatar)
                .into(imageView)
        }
    }

    @JvmStatic
    fun getRangTitleByXp(xp: Int, context: Context): String {
        val rang = getRang(xp)
        return context.resources.getStringArray(R.array.rang)[rang.id]
    }

    fun getRangTitleById(id: Int, context: Context): String {
        return context.resources.getStringArray(R.array.rang)[id]
    }

    fun getDays(profileModel: ProfileModel): String {
        return if (profileModel.registration != null) {
            getDays(profileModel.registration)
        } else "null"
    }

    @JvmStatic
    fun getDays(date: Instant?): String {
        val days = ((Instant.now().toEpochMilli() - date!!.toEpochMilli())
                / (1000 * 60 * 60 * 24)).toInt()
        return days.toString() + " " + getDayAddition(days)
    }

    private fun getDayAddition(num: Int): String {
        val preLastDigit = num % 100 / 10
        return if (preLastDigit == 1) {
            "дней"
        } else when (num % 10) {
            1 -> "день"
            2, 3, 4 -> "дня"
            else -> "дней"
        }
    }
}