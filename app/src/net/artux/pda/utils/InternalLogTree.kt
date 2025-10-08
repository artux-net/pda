package net.artux.pda.utils

import android.util.Log
import timber.log.Timber

class InternalLogTree : Timber.Tree() {
        val history = mutableListOf<String>()

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            val finalMessage = if (tag != null && tag.isNotBlank())
                "$tag : $message"
            else
                message
            history.add(finalMessage)
            if(history.size > 200)
                history.removeFirst()
        }
    }