package com.mediaplatform.manager;

import org.jboss.solder.resourceLoader.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Properties;

/**
 * User: timur
 * Date: 11/29/12
 * Time: 4:37 PM
 */
@ApplicationScoped
@Named
public class ConfigBean implements Serializable{
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

    public String getMediaServerStatUrl() {
        return props.getProperty("media.server.api.stat.url");
    }

    public String getNginxServerHost(){
        return props.getProperty("nginx.server.host");
    }
}
