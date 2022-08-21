package net.artux.pda.di;

import android.content.Context;

import net.artux.pda.app.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final App context;

    public ContextModule(App context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context context() {
        return context;
    }

    ;
}
