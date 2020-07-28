package java8.play2.example.servicescore;

import java8.play2.example.aws.ExampleCloudWatchObserver;

import java.util.concurrent.Callable;

public interface ExampleCallableServiceInterface extends Callable {
    String getSomeSting();
    void setSomeString(String stringSetter);
    void callSomeProcessingMethodOfService();
    void setDefaultCloudWatchObserver(ExampleCloudWatchObserver monitoring);

    // Example of nested interface.
    interface Food {
        String PIZZA = "pizza";
        String ICECREAM = "icecream";
    }
}
