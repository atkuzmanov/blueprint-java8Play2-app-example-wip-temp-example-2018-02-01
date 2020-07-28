package java8.play2.example.utilities;

import org.junit.Test;
import org.junit.Assert;

public class ExampleUtilsTest {
    @Test
    public void environmentNameTest() throws Exception {
        Assert.assertEquals("test", ExampleUtils.exampleEnviroNameWithFallback("test"));
        Assert.assertEquals("integration", ExampleUtils.exampleEnviroNameWithFallback("integration"));
        Assert.assertEquals("integration", ExampleUtils.exampleEnviroNameWithFallback("development"));
        Assert.assertEquals("integration", ExampleUtils.exampleEnviroNameWithFallback(""));
    }
}