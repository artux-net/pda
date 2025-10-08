package net.artux.pda.utils

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.Serializable


inline fun <reified T : Serializable> Bundle.serializable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializable(key, T::class.java)

        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

inline fun <reified T : Serializable> Intent.serializable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializableExtra(key, T::class.java)

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

inline fun drawableFromAssets(context: Context, path: String): Drawable? {
    var inputStream: InputStream? = null
    try {
        inputStream = context.assets.open(path)
        return Drawable.createFromStream(inputStream, null)
    } catch (e: IOException) {
        Timber.w(e)
    } finally {
        inputStream?.close()
    }
    return null
}
