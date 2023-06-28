package net.artux.pda.repositories

import net.artux.pda.utils.InternalLogTree
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val internalLogTree: InternalLogTree
) {

    fun getLogs(): List<String> {
        return internalLogTree.history
    }
}