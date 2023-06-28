package net.artux.pda.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.artux.pda.BuildConfig
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            val finalMessage = if (tag != null && tag.isNotBlank())
                "$tag : $message"
            else
                message

            FirebaseCrashlytics.getInstance().log(finalMessage)
            if (t != null && !BuildConfig.DEBUG) {
                FirebaseCrashlytics.getInstance().recordException(t)
                t.printStackTrace()
            }
        }
    }