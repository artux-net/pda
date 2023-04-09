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
import net.artux.pda.BuildConfig
import net.artux.pda.common.PropertyFields
import net.artux.pda.utils.FileLogTree
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
    fun properties(
        @ApplicationContext context: Context,
        remoteConfig: FirebaseRemoteConfig
    ): Properties {
        val properties = Properties()
        try {
            properties.load(context.assets.open("assets/config/app.properties"))
        } catch (e: IOException) {
            Timber.i("Props not found.")
        }
        properties[PropertyFields.API_URL] = BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API
        properties[PropertyFields.RESOURCE_URL] = BuildConfig.PROTOCOL + "://" + BuildConfig.URL
        properties.putAll(remoteConfig.all.mapValues { it.value.asString() })
        return properties
    }

    @Provides
    @Singleton
    fun logForest(): List<Timber.Tree> {
        val list = LinkedList<Timber.Tree>()
        list.add(if (BuildConfig.DEBUG) DebugTree() else CrashReportingTree())
        list.add(FileLogTree())
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