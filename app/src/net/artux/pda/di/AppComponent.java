package net.artux.pda.di;

import net.artux.pda.repositories.QuestRepository;
import net.artux.pda.repositories.SummaryRepository;
import net.artux.pda.repositories.UserRepository;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.api.DefaultApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {CacheModuleImpl.class, NetworkModule.class, ContextModule.class})
public interface AppComponent {

    UserRepository userRepository();

    QuestRepository questRepository();

    SummaryRepository summaryRepository();

    DefaultApi defaultApi();

    PdaAPI oldApi();
}
