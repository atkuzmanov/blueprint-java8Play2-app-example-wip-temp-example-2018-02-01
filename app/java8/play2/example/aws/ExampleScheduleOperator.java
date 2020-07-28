package java8.play2.example.aws;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

import com.google.inject.Singleton;
import com.google.inject.Inject;

import java.util.Timer;

import play.libs.F;
import play.inject.ApplicationLifecycle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Singleton
public class ExampleScheduleOperator {

    private static final Config CONFIG = ConfigFactory.load();

    private static final String DEFAULT_SCHED_PERIOD = "default.example.sched.period.set.in.app.conf.file";
    private static final Config DEFAULT_CONFIG = ConfigFactory.load();
    private static final String DEFAULT_SCHED_DELAY = "default.example.sched.delay.set.in.app.conf.file";
    private static final Logger LOG = LoggerFactory.getLogger(ExampleScheduleOperator.class);

    private boolean isDefaultSchedulerRunning = true;

    private final Timer timer = new Timer("Timer for consuming SQS messages.");

    private final ExampleSqsOperator defaultExampleSqsOperator;

    @Inject
    private ExampleCloudWatchObserver defaultClientForCloudWatch;

    public boolean isDefaultSchedulerRunning() {
        return isDefaultSchedulerRunning;
    }

    public void setIsDefaultSchedulerRunning(boolean schedulerRunning) {
        isDefaultSchedulerRunning = schedulerRunning;
    }

    /**
     * When the app stops, this consturctor shutsdown the timer to avoid memory leaks.
     * @param applicationLifecycle
     */
    @Inject
    public ExampleScheduleOperator(ApplicationLifecycle applicationLifecycle, ExampleSqsOperator defaultExampleSqsOperator) {
        this.defaultExampleSqsOperator = defaultExampleSqsOperator;

        applicationLifecycle.addStopHook(() -> {
            LOG.info("Shut down...");
            this.stop();
            defaultClientForCloudWatch.shutdown();
            return F.Promise.pure(null);
        });
    }

    public void stop() {
        try{
            LOG.info("Terminating background scheduler...");
            timer.cancel();
            LOG.info("Background scheduler terminated.");
        }catch(Exception ex) {
            LOG.warn("Termination of scheduler failed with exceptional state: ", ex);
        }
    }

    public void start() {
        try{
            LOG.info("Turning on ExampleSqsOperator with a timer task...");
            timer.schedule(defaultExampleSqsOperator, DEFAULT_CONFIG.getInt(DEFAULT_SCHED_DELAY), DEFAULT_CONFIG.getInt(DEFAULT_SCHED_PERIOD));
        }catch(Exception ex){
            System.out.println("App terminated.");
            LOG.error("Scheduler broke: ", ex);
            System.exit(0);
        }
    }
}
