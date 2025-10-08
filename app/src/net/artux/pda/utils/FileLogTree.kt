package net.artux.pda.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileLogTree @Inject constructor(
    @ApplicationContext val context: Context
) : Timber.Tree() {

    var ignored = false
    val fileName = "pda.log"
    private val timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (ignored)
            return

        if (message.isNullOrEmpty() && t == null)
            return

        try {
            val file = getFile()
            if (!file.exists())
                file.createNewFile()
            val time = Instant.now()
            if (file.exists()) {
                val fos = FileOutputStream(file, true)
                fos.write("${timeFormatter.format(time)}: $message\n".toByteArray(Charsets.UTF_8))
                if (t!= null)
                    fos.write(t.stackTraceToString().toByteArray(Charsets.UTF_8))
                fos.close()
            }
        } catch (e: IOException) {
            if (e is FileNotFoundException)
                ignored = true
            Log.println(Log.ERROR, "FileLogTree", "Error while logging into file: $e")
        }
    }

    fun resetFile() {
        getFile().delete()
    }

    fun getFile(): File {
        val directory = context.filesDir
        return File(directory, fileName)
    }

    fun getAllLogs(): List<String> {
        val file = getFile()
        return if (file.exists())
            file.readLines()
        else emptyList()
    }

    fun copyLogs(){
        val directory =
            Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}/logs")

        if (!directory.exists())
            directory.mkdirs()

        val fileName = "pdaLog.txt"

        val file = File("${directory.absolutePath}${File.separator}$fileName")
        getFile().copyTo(file, true)
    }

}