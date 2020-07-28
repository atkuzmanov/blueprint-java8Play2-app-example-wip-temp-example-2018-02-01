package java8.play2.example.domain.aws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleMsgContentObj {

    @JsonProperty("some_id")
    private String someId;

    @JsonProperty("default_time_stamp")
    private long defaultTimeStamp;

    public void setSomeId(String someId) {
        this.someId = someId;
    }

    public String getSomeId() {
        return someId;
    }

    public long getDefaultTimeStamp() {
        return defaultTimeStamp;
    }

    public void setDefaultTimeStamp(long defaultTimeStamp) {
        this.defaultTimeStamp = defaultTimeStamp;
    }
}