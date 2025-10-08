package net.artux.pda.repositories

import net.artux.pda.utils.FileLogTree
import net.artux.pda.utils.InternalLogTree
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val fileLogTree: FileLogTree
) {

    fun resetFile() {
        fileLogTree.resetFile()
    }

    fun getLogs(): List<String> =
        fileLogTree.getAllLogs()

    fun getLogFile(): File =
        fileLogTree.getFile()
}