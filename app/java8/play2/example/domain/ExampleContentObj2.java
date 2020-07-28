package java8.play2.example.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlType(propOrder={"someId","someInt","someState"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleContentObj2 {

    @JsonProperty("someId")
    private String someId;

    private int someInt;

    private long timeStamp;

    @JsonProperty("someState")
    private String someState;

    @JsonProperty("proceedWithProcessingVerificationCounterpart1Received")
    private Boolean proceedWithProcessingVerificationCounterpart1Received = false;

    @JsonProperty("proceedWithProcessingVerificationCounterpart2Received")
    private Boolean proceedWithProcessingVerificationCounterpart2Received = false;

    @DynamoDBHashKey(attributeName = "someState")
    public String getSomeState() {
        return someState;
    }

    public void setSomeState(String someState) {
        this.someState = someState;
    }
    @DynamoDBHashKey(attributeName = "someId")
    public String getSomeId() {
        return someId;
    }

    public void setSomeId(String someId) {
        this.someId = someId;
    }
    @DynamoDBHashKey(attributeName = "proceedWithProcessingVerificationCounterpart1Received")
    public Boolean getProceedWithProcessingVerificationCounterpart1Received() {
        return proceedWithProcessingVerificationCounterpart1Received;
    }

    public void setProceedWithProcessingVerificationCounterpart1Received(Boolean proceedWithProcessingVerificationCounterpart1Received) {
        this.proceedWithProcessingVerificationCounterpart1Received = proceedWithProcessingVerificationCounterpart1Received;
    }
    @DynamoDBHashKey(attributeName = "proceedWithProcessingVerificationCounterpart2Received")
    public Boolean getProceedWithProcessingVerificationCounterpart2Received() {
        return proceedWithProcessingVerificationCounterpart2Received;
    }

    public void setProceedWithProcessingVerificationCounterpart2Received(Boolean proceedWithProcessingVerificationCounterpart2Received) {
        this.proceedWithProcessingVerificationCounterpart2Received = proceedWithProcessingVerificationCounterpart2Received;
    }

    public int getSomeInt() {
        return someInt;
    }
    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<String> getSomeList() {
        return new ArrayList<>();
    }
}
