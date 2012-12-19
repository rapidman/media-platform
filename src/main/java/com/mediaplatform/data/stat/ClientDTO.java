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
}
