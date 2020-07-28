package java8.play2.example.factory;

import java8.play2.example.factory.ExampleCallableServiceFactory;
import java8.play2.example.servicescore.ExampleCallableServiceImplementation1;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleCallableServiceInterfaceFactoryTest {
    @Test
    public void fabricateServiceTest(){
        Assert.assertEquals(ExampleCallableServiceFactory.fabricateService("examplecallableserviceimplementation1").getClass(), ExampleCallableServiceImplementation1.class);
        assertEquals(ExampleCallableServiceFactory.fabricateService("examplesocialflowtwitterservice").getClass(), ExampleCallableServiceImplementation1.class);
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionForNonExistentServiceTest() {
        ExampleCallableServiceFactory.fabricateService("non-existent");
    }
}