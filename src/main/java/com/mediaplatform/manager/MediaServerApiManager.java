package com.mediaplatform.manager;

import com.mediaplatform.data.stat.ApplicationDTO;
import com.mediaplatform.data.stat.LiveDTO;
import com.mediaplatform.data.stat.RtmpDTO;
import com.mediaplatform.data.stat.ServerDTO;
import com.mediaplatform.util.URLReader;
import com.thoughtworks.xstream.XStream;

import javax.inject.Named;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:02 PM
 */
@Named
public class MediaServerApiManager extends AbstractManager {
    private RtmpDTO statInfo;

    public RtmpDTO getStatInfo() {
        if(statInfo == null){
            XStream xstream = getXstream();
            String xml = URLReader.read(configBean.getMediaServerStatUrl());
            statInfo = (RtmpDTO) xstream.fromXML(xml);
        }
        return statInfo;
    }

    public XStream getXstream() {
        XStream xstream = new XStream();
        xstream.processAnnotations(ApplicationDTO.class);
        xstream.processAnnotations(LiveDTO.class);
        xstream.processAnnotations(RtmpDTO.class);
        xstream.processAnnotations(ServerDTO.class);
        return xstream;
    }

    public static void main(String[] args){
        MediaServerApiManager manager = new MediaServerApiManager();
        RtmpDTO res = manager.getStatInfo();
        System.out.println(res);
    }
}
