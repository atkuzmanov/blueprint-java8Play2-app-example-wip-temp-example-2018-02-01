package java8.play2.example.factory;

import java8.play2.example.servicescore.ExampleCallableServiceImplementation1;
import java8.play2.example.servicescore.ExampleCallableServiceInterface;
import java8.play2.example.servicescore.ExampleSocialFlowTwitterService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ExampleCallableServiceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleCallableServiceFactory.class);

    static public ExampleCallableServiceInterface fabricateService(String serviceName) {
        serviceName = serviceName.toLowerCase();
        switch (serviceName) {
            case ("examplecallableserviceimplementation1"):
                return new ExampleCallableServiceImplementation1();
            case ("examplesocialflowtwitterservice"):
                return new ExampleSocialFlowTwitterService();
            default:
                LOGGER.error("Service not found.");
                throw new RuntimeException("Service not found.");
        }
    }
}
