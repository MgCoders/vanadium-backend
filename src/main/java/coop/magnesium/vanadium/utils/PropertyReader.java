package coop.magnesium.vanadium.utils;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rsperoni on 13/09/17.
 */
@Dependent
public class PropertyReader {

    public static final String RANGO_TRACKING_INI = "RANGO_TRACKING_INI";
    public static final String RANGO_TRACKING_FIN = "RANGO_TRACKING_FIN";
    public static final String RANGO_TRACKING_START_CODE = "RANGO_TRACKING_START_CODE";
    public static final String RANGO_TRACKING_END_CODE = "RANGO_TRACKING_END_CODE";

    @Produces
    public String jbossNodeName(){
        return System.getProperty("jboss.node.name");
    }

    @Produces
    @PropertiesFromFile
    public Properties provideServerProperties(InjectionPoint ip) {
        //get filename from annotation
        String filename = ip.getAnnotated().getAnnotation(PropertiesFromFile.class).value();
        return readProperties(filename);
    }

    private Properties readProperties(String fileInClasspath) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileInClasspath);

        try {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            System.err.println("Could not read properties from file " + fileInClasspath + " in classpath. " + e);
        }

        return null;
    }
}