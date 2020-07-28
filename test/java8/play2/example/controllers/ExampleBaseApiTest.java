package java8.play2.example.controllers;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.InjectMocks;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExampleBaseApiTest {
    @InjectMocks
    ExampleDefaultApi testApp;

    @Ignore
    public void testServiceStatus() {
        assertEquals(testApp.defaultExampleStatus().status(), 200);
    }

    @Test
    public void testIndexReturns() {
        assertEquals(testApp.defaultExampleIndex().status(), 200);
    }
}
