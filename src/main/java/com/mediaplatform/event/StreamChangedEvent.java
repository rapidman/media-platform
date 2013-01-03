package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 11/28/12
 * Time: 8:03 PM
 */
@Veto
public class StreamChangedEvent {
    private String streamName;

    public StreamChangedEvent(String streamName) {
        this.streamName = streamName;
    }

    public String getStreamName() {
        return streamName;
    }
}
