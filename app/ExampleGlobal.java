import java8.play2.example.utilities.ExampleEnvConfiguration;

import com.fasterxml.jackson.databind.node.TextNode;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class ExampleGlobal extends GlobalSettings {
    @Override
    public void beforeStart(Application app) {
        ExampleEnvConfiguration.defaultExampleSetEnvProps(new TextNode("{}"));
        ExampleEnvConfiguration.setEnvironmentVariables();
    }

    @Override
    public void onStart(Application app) {
        Logger.info("App starting up. . . ");
    }

    @Override
    public void onStop(Application app) {
        Logger.info("App shut down.");
    }
}
