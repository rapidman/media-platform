package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:08 PM
 */
@XStreamAlias("rtmp")
public class RtmpDTO {
    @XStreamAsAttribute
    private String version;
    @XStreamAsAttribute
    private String compiler;
    @XStreamAsAttribute
    private String built;
    @XStreamAsAttribute
    private String pid;
    @XStreamAsAttribute
    private int uptime;
    @XStreamAsAttribute
    private long in;
    @XStreamAsAttribute
    private long out;
    @XStreamAsAttribute
    private long bwin;
    @XStreamAsAttribute
    private long bwout;
    @XStreamAlias("server")
    private ServerDTO server;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getUptime() {
        return uptime;
    }

    public void setUptime(int uptime) {
        this.uptime = uptime;
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

    public ServerDTO getServer() {
        if(server == null){
            server = new ServerDTO();
        }
        return server;
    }

    public void setServer(ServerDTO server) {
        this.server = server;
    }
}
