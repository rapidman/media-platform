package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:15 PM
 */
@XStreamAlias("application")
public class ApplicationDTO {
    @XStreamAlias("name")
    private String name;
    @XStreamAlias("live")
    private LiveDTO live;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LiveDTO getLive() {
        return live;
    }

    public void setLive(LiveDTO live) {
        this.live = live;
    }
}
