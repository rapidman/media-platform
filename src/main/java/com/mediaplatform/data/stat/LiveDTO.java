package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

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
    private List<StreamDTO> stream;

    public int getNclients() {
        return nclients;
    }

    public void setNclients(int nclients) {
        this.nclients = nclients;
    }
}
