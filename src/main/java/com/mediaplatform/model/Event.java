package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.*;
import java.util.Date;

/**
 * User: timur
 * Date: 1/3/13
 * Time: 12:21 AM
 */
@Cacheable
@Entity
@Table(name = "event")
@Veto
public class Event extends AbstractEntity{
    private EventType type;
    private String addr;
    private String app;
    private String flashVer;
    private String swfUrl;
    private String tcUrl;
    private String pageUrl;
    private String streamName;
    private String contentName;
    private User user;
    private Date createDateTime = new Date();

    public Event(EventType type,
                 String addr,
                 String app,
                 String flashVer,
                 String swfUrl,
                 String tcUrl,
                 String pageUrl,
                 String streamName,
                 String contentName) {
        this();
        this.type = type;
        this.addr = addr;
        this.app = app;
        this.flashVer = flashVer;
        this.swfUrl = swfUrl;
        this.tcUrl = tcUrl;
        this.pageUrl = pageUrl;
        this.streamName = streamName;
        this.contentName = contentName;
    }

    public Event() {
        super(EntityType.EVENT);
    }

    @Enumerated(EnumType.STRING)
    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getFlashVer() {
        return flashVer;
    }

    public void setFlashVer(String flashVer) {
        this.flashVer = flashVer;
    }

    public String getSwfUrl() {
        return swfUrl;
    }

    public void setSwfUrl(String swfUrl) {
        this.swfUrl = swfUrl;
    }

    public String getTcUrl() {
        return tcUrl;
    }

    public void setTcUrl(String tcUrl) {
        this.tcUrl = tcUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public String getUserName(){
        return user == null ? "unknown" : user.getUsername();
    }
}
