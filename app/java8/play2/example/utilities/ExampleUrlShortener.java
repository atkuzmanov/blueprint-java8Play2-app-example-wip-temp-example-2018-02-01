package java8.play2.example.utilities;

import java8.play2.example.aws.ExampleCloudWatchObserver;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

@Singleton
public class ExampleUrlShortener {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleUrlShortener.class);
    private static final String BITLY_SHORTEN_SERVICES_ENDPOINT = "https://api-ssl.bitly.com/v3/shorten";
    private static final String BITLY_ACCESS_TOKEN = "bitly.urlShortener.accessToken";

    private static final int REQUEST_TIMEOUT = 30000;
    private ExampleCloudWatchObserver monitor;

    /**
     * Method for shortening a url using bitly
     *
     * @param fullUrl url to be shortened
     *
     * @return a shortened url, if any; throw an exception if not (null shouldn't be used
     * as a return value here as we want it to fail)
     */

    public String shorten(String fullUrl) throws Exception{

        if(StringUtils.isEmpty(fullUrl)) {
            monitor.alarmOff("UrlShortenerFailure");
            throw new IllegalArgumentException("Full Url must be provided for shortening service");
        }

        String bitlyAccessToken = ExampleUtils.exampleGetSystemProp(BITLY_ACCESS_TOKEN);

        if (StringUtils.isEmpty(bitlyAccessToken)) {
            monitor.alarmOff("UrlShortenerFailure");
            throw new IllegalStateException("Bitly access token not found (property is not set: " + ExampleUtils.appendLiveProductionSuffix(BITLY_ACCESS_TOKEN) + ")");
        }

        Promise<WSResponse> res = WS.url(BITLY_SHORTEN_SERVICES_ENDPOINT)
                .setQueryParameter("access_token", bitlyAccessToken)
                .setQueryParameter("longUrl", fullUrl)
                .setRequestTimeout(REQUEST_TIMEOUT)
                .get();

        JsonNode responseNode = res.get(REQUEST_TIMEOUT).asJson();
        JsonNode shortenedUrlNode = responseNode.findValue("url");

        if(shortenedUrlNode == null){
            monitor.alarmOff("UrlShortenerFailure");

            LOG.error("Unable to shorten the URL.  status_code -> " +
                        responseNode.findValue("status_code").asText() +
                        " status_text "+  responseNode.findValue("status_txt").asText());

           throw new Exception("Couldn't find 'url' value in jsonNode prodcued for Url shorter");
        }
        else {
            LOG.info("Shortened the URL -> "+ shortenedUrlNode.textValue());
            monitor.alarmOn("UrlShortenerFailure");
            return shortenedUrlNode.textValue();
        }
    }

    public void setMonitor(ExampleCloudWatchObserver monitor) {
        this.monitor = monitor;
    }
}
