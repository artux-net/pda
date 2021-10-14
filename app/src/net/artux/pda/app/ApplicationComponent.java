package net.artux.pda.app;

import net.artux.pda.ui.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = { App.class })
interface AppComponent extends AndroidInjector<App> {

    void inject(MainActivity mainActivity);

    final class Initializer {
        private Initializer() { }

        /*public static AppComponent init(App app) {
            *//*return DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .build();*//*
        }*/
    }
}