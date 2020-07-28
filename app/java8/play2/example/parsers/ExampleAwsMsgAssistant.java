package java8.play2.example.parsers;

import java8.play2.example.domain.aws.ExampleMsgContentObj;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.services.sqs.model.Message;

import java8.play2.example.domain.aws.ExampleAwsSqsMsgObj;

public class ExampleAwsMsgAssistant {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleAwsMsgAssistant.class);

    public ExampleMsgContentObj extractMsgContentObj(Message msg) {
        ExamplePlay2FrameworkJsonMapper mapper = new ExamplePlay2FrameworkJsonMapper();

        ExampleAwsSqsMsgObj exampleAwsSqsMsgObj;
        ExampleMsgContentObj defaultExampleMsgContentObj;

        try {
            exampleAwsSqsMsgObj = (ExampleAwsSqsMsgObj) mapper.read(msg.getBody(), ExampleAwsSqsMsgObj.class);
        } catch (Exception ex) {
            LOG.warn("Malformed sqs message generated exception: " + ex);
            return null;
        }

        try {
            defaultExampleMsgContentObj = (ExampleMsgContentObj) mapper.read(exampleAwsSqsMsgObj.getMsg(), ExampleMsgContentObj.class);
        } catch (Exception ex) {
            LOG.warn("Malformed sqs message contents generated exception: " + ex);
            return null;
        }
        return defaultExampleMsgContentObj;
    }
}
