package com.mediaplatform.util;

import java.util.Arrays;
import java.util.List;

/**
 * User: timur
 * Date: 12/10/12
 * Time: 8:10 PM
 */
public enum RtmpPublishFormat {
    FLV_LOW(Arrays.asList("-i", "%MEDIA_FILE%",
            "-scodec", "copy", "-f", "flv", "-vb", "400k", "rtmp://localhost/myapp/%s")),
    FLV_HIGH(Arrays.asList("-re", "-i", "%MEDIA_FILE%",
            "-acodec", "copy", "-vcodec", "copy", "-f", "flv", "rtmp://localhost/myapp/%s"));

    private List<String> args;

    RtmpPublishFormat(List<String> args){
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }
}