package java8.play2.example.utilities;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.util.IOUtils;

public class ExampleUtils {

    private final Logger LOG = LoggerFactory.getLogger(ExampleUtils.class);

    public static String exampleGetSystemProp(String name) {
        String qualifiedName = appendLiveProductionSuffix(name);
        return System.getProperty(qualifiedName);
    }

    public String exampleGetResource(String nameOfFile) throws IOException {
        try {
            return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(nameOfFile));
        } catch (IOException e) {
            LOG.error("IOException encountered when tried loading resource file." + e);
            throw e;
        } catch (Exception ex) {
            LOG.error("Exception!" + ex);
            throw ex;
        }
    }

    public static String exampleEnviroNameWithFallback(String env) {
        return ((StringUtils.isBlank(env)) || (env.equals("development"))) ? "integration" : env;
    }

    public static String appendLiveProductionSuffix(String propNameNoSuffix) {
        String enviro = System.getProperty("DEFAULT_SYSTEM_ENV");
        if(StringUtils.isNotEmpty(enviro) && enviro.equals("production")){
            return propNameNoSuffix.concat(".live");
        }
        return propNameNoSuffix;
    }
}
