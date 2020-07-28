package modules;

import java8.play2.example.aws.ExampleScheduleOperator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton
public class ExampleStartupConfigImplementation {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleStartupConfigImplementation.class);

    @Inject
    public ExampleStartupConfigImplementation(ExampleScheduleOperator scheduleManager) {
        LOG.info("Starting up the scheduler...");
        scheduleManager.start();
    }
}
