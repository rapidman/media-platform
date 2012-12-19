package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:33 PM
 */
@XStreamAlias("stream")
public class StreamDTO {
    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private long time;
    @XStreamAsAttribute
    private long in;
    @XStreamAsAttribute
    private long out;
    @XStreamAsAttribute
    private long bwin;
    @XStreamAsAttribute
    private long bwout;
    @XStreamImplicit
    private List<ClientDTO> clients;
    @XStreamAlias("meta")
    private MetaDTO meta;
    @XStreamAsAttribute
    private int nclients;
    @XStreamAlias("publishing")
    private PublishingDTO publishing;
    @XStreamAlias("active")
    private ActiveDTO active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getIn() {
        return in;
    }

    public void setIn(long in) {
        this.in = in;
    }

    public long getOut() {
        return out;
    }

    public void setOut(long out) {
        this.out = out;
    }

    public long getBwin() {
        return bwin;
    }

    public void setBwin(long bwin) {
        this.bwin = bwin;
    }

    public long getBwout() {
        return bwout;
    }

    public void setBwout(long bwout) {
        this.bwout = bwout;
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public MetaDTO getMeta() {
        return meta;
    }

    public void setMeta(MetaDTO meta) {
        this.meta = meta;
    }

    public int getNclients() {
        return nclients;
    }

    public void setNclients(int nclients) {
        this.nclients = nclients;
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
