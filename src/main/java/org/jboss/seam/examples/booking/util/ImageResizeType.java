package org.jboss.seam.examples.booking.util;

import org.jboss.solder.core.Veto;

/**
 * User: Sergey Demin
* Date: 11.11.2009
* Time: 13:15:29
*/
@Veto
public enum ImageResizeType {
    SQUARE ("s"),
    WIDTH ("x"),
    BIG_SIZE ("b"),
    RECTANGLE_CENTER ("rc")
    ;

    private final String code;

    ImageResizeType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String toString() {
        return this.code;
    }
}
