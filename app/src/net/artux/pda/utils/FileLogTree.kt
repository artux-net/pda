package net.artux.pda.utils

import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class FileLogTree : Timber.Tree() {

    var ignored = false

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (!ignored)
            try {
                val directory =
                    Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}/logs")

                if (!directory.exists())
                    directory.mkdirs()

                val fileName = "pdaLog.txt"

                val file = File("${directory.absolutePath}${File.separator}$fileName")

                file.createNewFile()

                if (file.exists()) {
                    val fos = FileOutputStream(file, true)

                    fos.write("$message\n".toByteArray(Charsets.UTF_8))
                    fos.close()
                }

            } catch (e: IOException) {
                if (e is FileNotFoundException)
                    ignored = true
                Log.println(Log.ERROR, "FileLogTree", "Error while logging into file: $e")
            }
    }
}