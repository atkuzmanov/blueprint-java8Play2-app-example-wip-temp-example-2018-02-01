package java8.play2.example.services;

import java.util.Calendar;

import java8.play2.example.services.SinceEpochTimeStampExample;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class SinceEpochTimeStampExampleTest {

    private SinceEpochTimeStampExample alertResult = new SinceEpochTimeStampExample(null);
    private Calendar exampleSince;

    private String convertCalendarToEpoch(Calendar since, int numberOfDays) {
        Calendar c = (Calendar) since.clone();
        c.add(Calendar.DAY_OF_MONTH, numberOfDays);
        return String.valueOf(c.getTimeInMillis());
    }

    @Before
    public void setup() {
        exampleSince = Calendar.getInstance();
        exampleSince.add(Calendar.DAY_OF_MONTH, -5);
    }

    @Test
    public void epochIsOutside5DaysAndIsNotValidTest() {
        Assert.assertFalse(alertResult.validateEpochForFiveDays(convertCalendarToEpoch(exampleSince, -1), exampleSince.getTime()));
    }

    @Test
    public void epochIsWithin5DaysAndIsValidTest() {
        Assert.assertTrue(alertResult.validateEpochForFiveDays(convertCalendarToEpoch(exampleSince, 1), exampleSince.getTime()));
    }

    @Test
    public void emptyEpochIsInvalidTest() {
        Assert.assertFalse(alertResult.validateEpochForFiveDays(null, exampleSince.getTime()));
        Assert.assertFalse(alertResult.validateEpochForFiveDays(" ", exampleSince.getTime()));
        Assert.assertFalse(alertResult.validateEpochForFiveDays("", exampleSince.getTime()));
    }

    @Test
    public void nonNumericEpochIsInvalidTest() {
        Assert.assertFalse(alertResult.validateEpochForFiveDays("foobar", exampleSince.getTime()));
    }
}