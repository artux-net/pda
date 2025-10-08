package net.artux.pda.utils

import timber.log.Timber
import java.io.PrintStream

class TimberPrintStream : PrintStream(System.out) {
    override fun println(s: String?) {
        Timber.i(s)
    }

    override fun print(s: String?) {
        Timber.i(s)
    }
}