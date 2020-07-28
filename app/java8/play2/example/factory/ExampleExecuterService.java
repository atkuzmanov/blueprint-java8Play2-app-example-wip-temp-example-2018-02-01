package java8.play2.example.factory;

import java8.play2.example.aws.ExampleCloudWatchObserver;
import java8.play2.example.aws.ExampleDynamoDbDaoLayer;
import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.servicescore.ExampleCallableServiceImplementation1;
import java8.play2.example.servicescore.ExampleCallableServiceInterface;
import java8.play2.example.services.ExampleMongoDBService;
import java8.play2.example.utilities.ExampleEnvConfiguration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.ArrayList;

import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton
public class ExampleExecuterService {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleExecuterService.class);

    private static final int EXAMPLE_EXECUTOR_TIMEOUT_THREAD_POOL_TERMINATION = 25;
    private static final int EXAMPLE_EXECUTOR_SIZE_OF_THREAD_POOL = 5;

    private final ExampleCloudWatchObserver monitoring;

    private final ExampleDynamoDbDaoLayer defaultDynamoDbAssistant;

    private final ExampleMongoDBService exampleMongoDBService;

    @Inject
    public ExampleExecuterService(ExampleCloudWatchObserver monitoring, ExampleMongoDBService exampleMongoDBService ) {
        this.monitoring = monitoring;
        this.defaultDynamoDbAssistant = ExampleEnvConfiguration.getDynamoClient();
        this.exampleMongoDBService = exampleMongoDBService;
    }

    public void process(ExampleContentObj2 someExampleContentObj1) {
        // Java executor service: java.util.concurrent.ExecutorService
        java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(EXAMPLE_EXECUTOR_SIZE_OF_THREAD_POOL);
        try {
            List<Future<ExampleCallableServiceInterface>> futures = executor.invokeAll(callableExampleCallableServiceInterfaces(someExampleContentObj1));
            futures.stream().filter(Future::isDone).forEach((f) -> {
                try {
                    Object object = f.get();
                    // do some processing with object obtained from Future...
                } catch (InterruptedException ex) {
                    LOG.error("InterruptedException: ", ex);
                } catch (ExecutionException ex) {
                    LOG.error("ExecutionException: ", ex);
                }
            });
        } catch (Exception ex) {
            LOG.error("Error: ", ex.getStackTrace());
            shutdownExecutorThreadpool(executor);
        }
        if (!executor.isShutdown()) {
            shutdownExecutorThreadpool(executor);
        }
    }

    /**
     * Reclaim system resources by forcing shutdown of the Executor service.
     * @param threadPool
     */
    private void shutdownExecutorThreadpool(ExecutorService threadPool) {
        LOG.info("Shutting down executor thread pool service.");
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(EXAMPLE_EXECUTOR_TIMEOUT_THREAD_POOL_TERMINATION, TimeUnit.SECONDS)) {
                LOG.error("Forcing shutdown of executor thread pool service.");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            LOG.error("Executor thread pool service couldn't shutdown normally.", ex);
            threadPool.shutdownNow();
        } catch (Exception ex) {
            LOG.error("Error! Executor thread pool service couldn't shutdown normally. Trying to force shut down.", ex);
            threadPool.shutdownNow();
        }
        LOG.info("ExecutorService threadpool shutdown completely.");
    }

    private List<Callable<ExampleCallableServiceInterface>> callableExampleCallableServiceInterfaces(ExampleContentObj2 exampleContentObj1) {
        List<Callable<ExampleCallableServiceInterface>> services = new ArrayList<>();
        for (String individualServiceId : exampleContentObj1.getSomeState().split(",")) {
            ExampleCallableServiceFactory.fabricateService(individualServiceId);
            ExampleCallableServiceInterface service = new ExampleCallableServiceImplementation1();
            service.callSomeProcessingMethodOfService();
            services.add(service);
        }
        return services;
    }
}