package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:17 PM
 */
@XStreamAlias("live")
public class LiveDTO {
    @XStreamAsAttribute
    private int nclients;
    @XStreamImplicit
    private List<StreamDTO> streams;

    public int getNclients() {
        return nclients;
    }

    public void setNclients(int nclients) {
        this.nclients = nclients;
    }

    public List<StreamDTO> getStreams() {
        if(streams == null){
            streams = new ArrayList<StreamDTO>();
        }
        return streams;
    }

    public void setStreams(List<StreamDTO> streams) {
        this.streams = streams;
    }
}
