package java8.play2.example.services;

import java8.play2.example.aws.ExampleDynamoDbDaoLayer;
import java8.play2.example.domain.ExampleContentObj2;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.*;
import java.text.SimpleDateFormat;

import com.google.inject.Inject;

public class SinceEpochTimeStampExample {

    private static final Logger LOG = LoggerFactory.getLogger(SinceEpochTimeStampExample.class);

    private static final Integer DAYS_PAST_NUMBER = -5;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    boolean validateEpochForFiveDays(String epochToValidate, Date since5Days) {
        if (StringUtils.isBlank(epochToValidate) || !StringUtils.isNumeric(epochToValidate))
            return false;
        return (new Date(NumberUtils.toLong(epochToValidate)).after(since5Days));
    }

    /** Sort list in descending order. */
    private void sortResults(List<ExampleContentObj2> results){
        if(results != null && !results.isEmpty()){
            Collections.sort(results, new Comparator<ExampleContentObj2>() {
                public int compare(ExampleContentObj2 a, ExampleContentObj2 b){
                    if(a.getTimeStamp() > b.getTimeStamp()){
                        return -1;
                    }
                    else if(a.getTimeStamp() == b.getTimeStamp()){
                        return 0 ;
                    }
                    return 1;
                }
            });
        }
    }

    public List<ExampleContentObj2> sinceLast5DaysIfEpochNotGiven(String epochAsString) {
        Calendar last5DaysCalendar = Calendar.getInstance();
        last5DaysCalendar.add(Calendar.DAY_OF_MONTH, DAYS_PAST_NUMBER);
        Date dateSince = last5DaysCalendar.getTime();
        if (validateEpochForFiveDays(epochAsString, dateSince)) {
            dateSince = new Date(NumberUtils.toLong(epochAsString));
        } else {
            if (StringUtils.isNotBlank(epochAsString))
                LOG.error("[" + epochAsString + "] - not valid epoch or outside last five days.");
        }
        LOG.debug("Query DynamoDB from formatted date output [" + simpleDateFormat.format(dateSince) + "] same as not formatted ["+dateSince.getTime()+"].");
        List<ExampleContentObj2> resultList = StreamSupport.stream(defaultDynamoDbDaoLayer.retrieveItemDefaultsSinceDate(dateSince)
                .spliterator(), false)
                .collect(Collectors.toList());
        sortResults(resultList);
        return resultList;
    }

    @Inject
    final ExampleDynamoDbDaoLayer defaultDynamoDbDaoLayer;

    @Inject
    SinceEpochTimeStampExample(ExampleDynamoDbDaoLayer defaultDynamoDbDaoLayer) {
          this.defaultDynamoDbDaoLayer = defaultDynamoDbDaoLayer;
    }
}
