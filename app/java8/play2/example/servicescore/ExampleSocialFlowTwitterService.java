package java8.play2.example.servicescore;

import java8.play2.example.aws.ExampleCloudWatchObserver;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;

import play.libs.ws.WSResponse;
import play.libs.ws.WS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.net.URI;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.scribe.model.*;

public class ExampleSocialFlowTwitterService implements ExampleCallableServiceInterface {

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

    private static final Logger LOG = LoggerFactory.getLogger(ExampleSocialFlowTwitterService.class);

    private static final int TIMEOUT_OF_REQUEST = 30000;
    private static final String SOCIALFLOW_API_TWITTER_URI = "https://api.socialflow.com/message/add";

    private ExampleCloudWatchObserver defaultCloudWatchObserver;

    @Override
    public Object call() throws Exception {
        try {
            int socialFlowResponseCode;
            String defaultExampleTweet = "{Test hello.}";
            defaultExampleTweet = exampleShorten(defaultExampleTweet, "www.example-url.com");

            Response socialFlowResponse = exampleSendToSocialFlow(defaultExampleTweet, "www.example-url.com");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(socialFlowResponse.getBody());

            if (socialFlowResponse.getCode() == 200) {
                socialFlowResponseCode = jsonNode.get("status").asInt();
            } else {
                socialFlowResponseCode = socialFlowResponse.getCode();
            }
            // Can be modified to return a content object or something else.
            return socialFlowResponseCode;
        } catch (Exception e) {
            LOG.error("Error: ", e);
            return 500;
        }
    }

    public String exampleShorten(String message, String url) {
        if (message != null) {
            int maximumLength;
            if (StringUtils.isBlank(url)) {
                maximumLength = 135;
            } else {
                maximumLength = 115;
            }
            if (message.length() > maximumLength) {
                message = message.substring(0, maximumLength);
            }
            return message;
        }
        LOG.warn("String is null.");
        return "";
    }

    /**
     * After posting our credentials, SocailFlow give us back the oauth_verifier query param
     * in the response header. This verifier will allow to connect to the SocialFlow api.
     */
    private String exampleExtractOauthVerifier(WSResponse wsResponse) throws Exception {
        if (wsResponse.getStatus() == 302) {
            String location = wsResponse.getHeader("Location");
            if (StringUtils.isNotBlank(location)) {
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(location), "UTF-8");
                for (NameValuePair param : params) {
                    if (param.getName().equalsIgnoreCase("oauth_verifier")) {
                        return param.getValue();
                    }
                }
            } else {
                defaultCloudWatchObserver.alarmOff("Error - tweeting failed!");
                throw new Exception("Error - tweeting failed due to problem getting oauth_verifier from SocialFlow redirect.");
            }
        }
        return null;
    }

    public Response exampleSendToSocialFlow(String message, String uriLink) throws Exception {
        Response exampleResponse;
        Token defaultRequestToken;
        Token defaultAccessToken;
        String defaultExampleOauthUrl;

        final OAuthService defaultOAuthService = new ServiceBuilder()
                .provider(ExampleSocialFlowApi.class)
                .apiKey("default example api key")
                .apiSecret("default example api secret")
                .callback("oauth_callback=foo")
                // .debug() //Can be used for additional debug output when debugging locally.
                .build();
        try {
            defaultRequestToken = defaultOAuthService.getRequestToken();
            defaultExampleOauthUrl = defaultOAuthService.getAuthorizationUrl(defaultRequestToken);
        } catch (Exception e) {
            LOG.error("Exception: ", e.getMessage());
            if (e.getCause() != null) LOG.error("Exception cause: " + e.getCause().getMessage());
            if (e.getStackTrace() != null) LOG.error("Exception stack trace: " + e.getStackTrace());
            throw e;
        }

        WSResponse wsResponse = WS.url(defaultExampleOauthUrl).setFollowRedirects(false)
                .setQueryParameter("__username", "default example user")
                .setQueryParameter("__password", "default example password]")
                .setQueryParameter("authorize", "authorize")
                .post("Blah...")
                .get(TIMEOUT_OF_REQUEST);

        if (wsResponse != null) {
            String oauthVerifier = exampleExtractOauthVerifier(wsResponse);
            if (StringUtils.isNotBlank(oauthVerifier)) {
                Verifier verifier = new Verifier(oauthVerifier);
                defaultAccessToken = defaultOAuthService.getAccessToken(defaultRequestToken, verifier);
                OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, SOCIALFLOW_API_TWITTER_URI);
                oAuthRequest.setConnectTimeout(10000, TimeUnit.MILLISECONDS);
                oAuthRequest.addQuerystringParameter("publish_option", "process now");
                oAuthRequest.addQuerystringParameter("message", message + " " + uriLink);
                oAuthRequest.addQuerystringParameter("service_user_id", "default example service user id"); // SocialFlow created id.
                oAuthRequest.addQuerystringParameter("shorten_links", "1");
                oAuthRequest.addQuerystringParameter("account_type", "twitter");
                defaultOAuthService.signRequest(defaultAccessToken, oAuthRequest);
                exampleResponse = oAuthRequest.send();
            } else {
                throw new Exception("Exception occurred while trying to get SocialFlow verifier token.");
            }
        } else {
            throw new Exception("Exception occurred while trying to connect to SocialFlow.");
        }
        return exampleResponse;
    }
}
