package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:35 PM
 */
@XStreamAlias("client")
public class ClientDTO {
    private String id;
    private String address;
    private long time;
    private String dropped;
    private String avsync;
    private String flashver;
    @XStreamAlias("publishing")
    private PublishingDTO publishing;
    @XStreamAlias("active")
    private ActiveDTO active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDropped() {
        return dropped;
    }

    public void setDropped(String dropped) {
        this.dropped = dropped;
    }

    public String getAvsync() {
        return avsync;
    }

    public void setAvsync(String avsync) {
        this.avsync = avsync;
    }

    public String getFlashver() {
        return flashver;
    }

    public void setFlashver(String flashver) {
        this.flashver = flashver;
    }

    public PublishingDTO getPublishing() {
        return publishing;
    }

    public void setPublishing(PublishingDTO publishing) {
        this.publishing = publishing;
    }

    public ActiveDTO getActive() {
        return active;
    }

    public void setActive(ActiveDTO active) {
        this.active = active;
    }
}
