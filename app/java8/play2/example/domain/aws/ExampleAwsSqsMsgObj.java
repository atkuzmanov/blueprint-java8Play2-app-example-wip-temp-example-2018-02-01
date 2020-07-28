package java8.play2.example.domain.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleAwsSqsMsgObj {

    @JsonProperty("someId")
    private String someId;

    @JsonProperty("msg")
    private String msg;

    public void setSomeId(String someId) {
        this.someId = someId;
    }

    public String getSomeId() {
        return someId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String Message) {
        this.msg = Message;
    }
}

