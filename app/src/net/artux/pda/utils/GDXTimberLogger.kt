package net.artux.pda.utils

import com.badlogic.gdx.ApplicationLogger
import timber.log.Timber

class GDXTimberLogger : ApplicationLogger {
    override fun log(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    override fun log(tag: String, message: String, exception: Throwable) {
        Timber.tag(tag).e(exception, message)
    }

    override fun error(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }

    override fun error(tag: String, message: String, exception: Throwable) {
        Timber.tag(tag).e(exception, message)
    }

    override fun debug(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun debug(tag: String, message: String, exception: Throwable) {
        Timber.tag(tag).e(exception, message)
    }
}