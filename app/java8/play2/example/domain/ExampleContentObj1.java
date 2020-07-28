package java8.play2.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleContentObj1 {

    // Ids should be Long, instead of Integer.
    private Integer exampleId;

    private String isFake;

    // When the name of the json property will be the same both on the in and out, getter and setter, then it can be set on the private variable.
    @JsonProperty("timestamp")
    private long timeStamp;

    // When the name of the json property will be different on the in and out, getter and setter, then it can be set on them individually.
    @JsonProperty("isItReallyFake")
    public void setIsFake(String isFake) {
        this.isFake = isFake;
    }

    @JsonProperty("isFake")
    public String getIsFake() {
        return isFake;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Integer getExampleId() {
        return exampleId;
    }

    public void setExampleId(Integer exampleId) {
        this.exampleId = exampleId;
    }

    @Override
    public String toString() {
        return "ClassPojo [isFake = " + isFake + "]";
    }
}