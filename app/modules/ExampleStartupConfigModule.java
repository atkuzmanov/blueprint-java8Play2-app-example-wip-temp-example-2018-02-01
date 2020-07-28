package modules;

import com.google.inject.AbstractModule;

import play.Logger;

public class ExampleStartupConfigModule extends AbstractModule {
    protected void configure() {
        Logger.info("Binding the scheduler on startup.");
        bind(ExampleStartupConfigInterface.class)
                .to((Class<? extends ExampleStartupConfigInterface>) ExampleStartupConfigImplementation.class)
                .asEagerSingleton();
    }
}