package net.artux.pda.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.artux.pda.BuildConfig
import net.artux.pda.common.PropertyFields
import net.artux.pda.repositories.UserRepository
import net.artux.pda.utils.CrashReportingTree
import net.artux.pda.utils.FileLogTree
import net.artux.pda.utils.InternalLogTree
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
        remoteConfig: FirebaseRemoteConfig,
        userRepository: UserRepository
    ): Properties {
        val properties = Properties()
        try {
            properties.load(context.assets.open("config/app.properties"))
        } catch (e: IOException) {
            Timber.i("Props not found.")
        }
        properties[PropertyFields.API_URL] = BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API
        properties[PropertyFields.RESOURCE_URL] = BuildConfig.PROTOCOL + "://" + BuildConfig.URL
        properties[PropertyFields.TESTER_MODE] = userRepository.isUserTester()
        properties.putAll(remoteConfig.all.mapValues { it.value.asString() })
        return properties
    }

    @Provides
    @Singleton
    fun logForest(internalLogTree: InternalLogTree): List<Timber.Tree> {
        val list = LinkedList<Timber.Tree>()
        list.add(internalLogTree)
        if (BuildConfig.DEBUG) {
            list.add(DebugTree())
            list.add(FileLogTree())
        } else
            list.add(CrashReportingTree())
        return list
    }

    @Provides
    @Singleton
    fun internalLogTree(): InternalLogTree {
        return InternalLogTree()
    }
}