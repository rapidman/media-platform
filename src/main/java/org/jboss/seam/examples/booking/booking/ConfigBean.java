package org.jboss.seam.examples.booking.booking;

import org.jboss.solder.resourceLoader.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Properties;

/**
 * User: timur
 * Date: 11/29/12
 * Time: 4:37 PM
 */
@ApplicationScoped
@Named
public class ConfigBean {
    @Inject
    @Resource("META-INF/app.properties")
    private Properties props;

    public String getFileStoragePath() {
        return props.getProperty("fileStoragePath");
    }

    public String getResourceServletMapping(){
        return props.getProperty("resource.servlet.mapping");
    }

    public String getStreamDropUrl() {
        return props.getProperty("stream.drop.url");
    }
}
