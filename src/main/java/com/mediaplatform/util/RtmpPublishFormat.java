package com.mediaplatform.util;

import java.util.Arrays;
import java.util.List;

/**
 * User: timur
 * Date: 12/10/12
 * Time: 8:10 PM
 */
public enum RtmpPublishFormat {
    MENCODER_COPY_TO_FLV(Arrays.asList("%SOURCE%", "-oac", "copy", "-ovc", "copy", "-o", "rtmp://localhost/myapp/%s")),
    FLV_LOW(Arrays.asList("-i", "%SOURCE%", "-scodec", "copy", "-f", "flv", "-vb", "1000k", "rtmp://localhost/myapp/%s")),
    FLV_HIGH(Arrays.asList("-re", "-i", "%SOURCE%", "-acodec", "copy", "-vcodec", "copy", "-f", "flv", "rtmp://localhost/myapp/%s")),
    HLS_TO_RTMP_FLV(Arrays.asList("-re", "-i", "%SOURCE%", "-c", "copy", "-f", "flv", "rtmp://localhost/myapp/%s"));

    private List<String> args;

    RtmpPublishFormat(List<String> args){
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }
}
