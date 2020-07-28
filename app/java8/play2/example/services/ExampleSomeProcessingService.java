package java8.play2.example.services;

import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.factory.ExampleExecuterService;
import java8.play2.example.servicescore.ExampleCallableServiceInterface;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.inject.Inject;

public class ExampleSomeProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSomeProcessingService.class);

    private final ExampleExecuterService executerService;
    private ExampleContentObj2 someExampleContentObj1;

    @Inject
    public ExampleSomeProcessingService(ExampleExecuterService executerService) {
        this.executerService = executerService;
    }

    public Boolean isItReadyForProcessing(ExampleContentObj2 someExampleContentObj1) {
        return someExampleContentObj1.getProceedWithProcessingVerificationCounterpart1Received() &&
                someExampleContentObj1.getProceedWithProcessingVerificationCounterpart2Received();
    }

    public Boolean isItOkayToProccess(ExampleContentObj2 someExampleContentObj1) {
        if(someExampleContentObj1.getSomeState() != null && !someExampleContentObj1.getSomeState().equals(ExampleCallableServiceInterface.Food.PIZZA)) {
            LOGGER.warn("Not processing:" + someExampleContentObj1.getSomeId() + " because it's state is :"+ someExampleContentObj1.getSomeState() +
                    ", so no further processing to prevent processing it more than once.");
            return false;
        }
        else {
            return true;
        }
    }

    public void someExampleProcessingMethod(ExampleContentObj2 someExampleContentObj1) {
        // if(some_other_check_is_true)
        {
            someExampleContentObj1.setProceedWithProcessingVerificationCounterpart1Received(true);
            someExampleContentObj1.setProceedWithProcessingVerificationCounterpart2Received(true);
        }
        if (isItOkayToProccess(someExampleContentObj1) && isItReadyForProcessing(someExampleContentObj1))
            executerService.process(someExampleContentObj1);
    }

    public ExampleContentObj2 getSomeExampleContentObj1() {
        return someExampleContentObj1;
    }
    public void setSomeExampleContentObj1(ExampleContentObj2 someExampleContentObj1) {
        this.someExampleContentObj1 = someExampleContentObj1;
    }
}
