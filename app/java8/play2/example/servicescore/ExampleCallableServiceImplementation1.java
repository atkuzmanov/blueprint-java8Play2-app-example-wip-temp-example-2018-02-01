package java8.play2.example.servicescore;

import java8.play2.example.aws.ExampleCloudWatchObserver;
import java8.play2.example.domain.ExampleContentObj2;

import com.amazonaws.util.json.JSONArray;

import play.libs.ws.WS;
import play.libs.F;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import org.apache.commons.lang.StringUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import com.amazonaws.util.json.JSONObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class ExampleCallableServiceImplementation1 implements ExampleCallableServiceInterface {
    @Override
    public String getSomeSting(){return "";}

    @Override
    public void setSomeString(String stringSetter){}

    @Override
    public void callSomeProcessingMethodOfService(){}

    @Override
    public void setDefaultCloudWatchObserver(ExampleCloudWatchObserver monitoring){
        this.defaultCloudWatchObserver = monitoring;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ExampleCallableServiceImplementation1.class);

    private static final int REQUEST_TIMEOUT_IN_MILLISECONDS = 30000;

    private ExampleCloudWatchObserver defaultCloudWatchObserver;

    @Override
    public Object call() throws Exception {
        // Example do some processing...

        // Example use of Java8 Optional
        Optional.ofNullable(getSomeSting()).orElse("");

        stringIsNotEmpty(exampleGenerateXMLStringUsingJAXB());

        String commaSeparatedString = "1,2,3,4,5";

        List<Integer> exampleField1payloadList = Collections.emptyList();
        exampleField1payloadList = Arrays.stream(commaSeparatedString.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());

        JSONObject payloadJson = new JSONObject();
        payloadJson.put("example_field1",  new JSONArray())
                .put("example_field2",  "example value 2");

        byte[] byteArrayFromString = "string to be encoded, converted to bytes array beforehand".getBytes();
        String base64EncodedString = java.util.Base64.getEncoder().encode(byteArrayFromString).toString();
        String base64EncodedString2 = org.apache.xerces.impl.dv.util.Base64.encode(byteArrayFromString);

        F.Promise<WSResponse> response = WS.url("http://www.example.url.com")
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", base64EncodedString)
                .setRequestTimeout(REQUEST_TIMEOUT_IN_MILLISECONDS)
                .setFollowRedirects(true)
                .post(payloadJson.toString());

        // Post request example:
        WSRequest exampleWsRequest = WS.url("http://www.example.url.com")
                .setFollowRedirects(true)
                .setRequestTimeout(90000)
                .setQueryParameter("debug", "0")
                .setQueryParameter("retry", "1");
        LOG.info("Sending request with these query parameters: " + exampleWsRequest.getQueryParameters());
        exampleWsRequest.post("Default example payload.");

        if (response != null) {
            WSResponse wsResponse = response.get(REQUEST_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS);

            if (wsResponse != null && wsResponse.getStatus() == 200 || wsResponse.getStatus() == 201) {
                LOG.info("Success: " + wsResponse.getBody());
                defaultCloudWatchObserver.alarmOn("ExampleCallableServiceImplementation1Failed");
            } else {
                LOG.error("Failure: " + " " + wsResponse.getStatus() + " " + wsResponse.getStatusText() + " " + wsResponse.getBody());
                defaultCloudWatchObserver.alarmOff("ExampleCallableServiceImplementation1Failed");
            }
        } else {
            defaultCloudWatchObserver.alarmOff("ExampleCallableServiceImplementation1Failed");
            // Could be replaced with a custom response object, below is just pseudo code example:
            // Object customResponseToBePropagatedToApi = new Object();
            // return this.customResponseToBePropagatedToApi(500, "Server error.");
            return null;
        }
        return null;
    }

    public String exampleGenerateXMLStringUsingJAXB() throws JAXBException {
        ExampleContentObj2 someExampleContentObj1 = new ExampleContentObj2();
        someExampleContentObj1.setSomeId("1");
        someExampleContentObj1.setSomeInt(1);
        someExampleContentObj1.setSomeState("ready");

        JAXBContext jaxbContext = JAXBContext.newInstance(ExampleContentObj2.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        java.io.StringWriter stringWriter = new StringWriter();

        stringWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(someExampleContentObj1, stringWriter);
        return stringWriter.toString();
    }

    public Boolean verifyMandatoryPropertiesAreSet(ExampleContentObj2 someExampleContentObj1) {
        return Arrays.asList(
                someExampleContentObj1.getSomeId(),
                someExampleContentObj1.getSomeState()
        ).stream().allMatch(StringUtils::isNotBlank);
    }

    private Boolean stringIsNotEmpty(String notEmptyString) {
        return (StringUtils.isNotEmpty(notEmptyString));
    }
}
