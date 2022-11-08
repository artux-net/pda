package net.artux.pda.di

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.bipi.tressence.file.FileLoggerTree
import net.artux.pda.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.IOException
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun properties(@ApplicationContext context: Context, remoteConfig: FirebaseRemoteConfig): Properties {
        val properties = Properties()
        try {
            properties.load(context.assets.open("config/app.properties"))
        } catch (e: IOException) {
            Timber.i("Props not found.")
        }
        properties.putAll(remoteConfig.all.mapValues { it.value.asString() })
        return properties
    }

    @Provides
    @Singleton
    fun logForest(@ApplicationContext context: Context): List<Timber.Tree> {
        val list = LinkedList<Timber.Tree>()
        list.add(if (BuildConfig.DEBUG) DebugTree() else CrashReportingTree())
        var filePath = context.filesDir.absolutePath + "/logs";
        list.add(
            FileLoggerTree.Builder()
                .withFileName("file.log")
                .withDirName(filePath)
                .withSizeLimit(20000)
                .withFileLimit(3)
                .appendToFile(true)
                .build()
        )
        return list
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            var message = message
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (tag != null) message = "$tag : $message"
            FirebaseCrashlytics.getInstance().log(message)
            if (t != null && !BuildConfig.DEBUG) {
                FirebaseCrashlytics.getInstance().recordException(t)
                t.printStackTrace()
            }
        }
    }
}