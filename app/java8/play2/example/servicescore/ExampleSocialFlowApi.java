package java8.play2.example.servicescore;

import org.scribe.model.Token;
import org.scribe.builder.api.DefaultApi10a;

public class ExampleSocialFlowApi extends DefaultApi10a {

    public static class Authenticate extends ExampleSocialFlowApi {
        private static final String SOCIALFLOW_AUTHENTICATE_URI = "https://www.socialflow.com/oauth/authorize?oauth_token=%s";
        @Override
        public String getAuthorizationUrl(Token requestToken)
        {
            return String.format(SOCIALFLOW_AUTHENTICATE_URI, requestToken.getToken());
        }
    }

    private static final String SOCIALFLOW_AUTHORIZE_URI = "https://www.socialflow.com/oauth/authorize?oauth_token=%s";
    private static final String SOCIALFLOW_REQUEST_TOKEN_URI = "https://www.socialflow.com/oauth/request_token";

    private static final String SOCIAFLOW_ACCESS_TOKEN_URI = "https://www.socialflow.com/oauth/access_token";

    @Override
    public String getAuthorizationUrl(Token token) {
        return String.format(SOCIALFLOW_AUTHORIZE_URI, token.getToken());
    }

    @Override
    public String getAccessTokenEndpoint() {
        return SOCIAFLOW_ACCESS_TOKEN_URI;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return SOCIALFLOW_REQUEST_TOKEN_URI;
    }
}
