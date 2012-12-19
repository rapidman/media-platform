package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:37 PM
 */
@XStreamAlias("meta")
public class MetaDTO {
    @XStreamAsAttribute
    private int width;
    @XStreamAsAttribute
    private int height;
    @XStreamAsAttribute
    private int framerate;
    @XStreamAsAttribute
    private String video;
    @XStreamAsAttribute
    private String audio;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
