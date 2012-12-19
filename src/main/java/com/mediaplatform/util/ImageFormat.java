package com.mediaplatform.util;

import org.jboss.solder.core.Veto;

/**
 * User: Sergey Demin
* Date: 11.11.2009
* Time: 13:13:04
*/
@Veto
public class ImageFormat {

    public static final int MAX_DIMENSION = 1920; //full hd, должно хватить под все нужды (в т.ч. и для ресайза wallpapers)
    public static final int MIN_DIMENSION = 16;
    private final int size;

    private final int size2;

    private final ImageResizeType resizeType;

    private final FileFormat format;

    public ImageFormat(int size, ImageResizeType resizeType) {
        this(size, resizeType, FileFormat.JPG);
    }

    public ImageFormat(int size, ImageResizeType resizeType, FileFormat format) {
        this.size = size;
        this.size2 = 0;
        this.resizeType = resizeType;
        this.format = format;
    }

    public ImageFormat(int size, int size2, ImageResizeType resizeType) {
        this(size, size2, resizeType, FileFormat.JPG);
    }

    public ImageFormat(int size, int size2, ImageResizeType resizeType, FileFormat format) {
        this.size = size;
        this.size2 = size2;
        this.resizeType = resizeType;
        this.format = format;
    }

    public ImageResizeType getResizeType() {
        return resizeType;
    }

    public int getSize() {
        return size;
    }

    public int getSize2() {
        return size2;
    }
    public FileFormat getFormat() {
        return format;
    }

    public boolean isTransparent() {
        return format == FileFormat.PNG;
    }

    public static ImageFormat parse(String value) {
        return parse(value, null);
    }

    public static ImageFormat parse(String value, FileFormat ff) {
        int ind = value.lastIndexOf(".");
        if (ind >= 0 && ind < value.length()-1){
            String tmp = value.substring(ind +1);
            value = value.substring(0,ind);
            if (ff == null) {
                for (FileFormat format : FileFormat.values()) {
                    if (tmp.equalsIgnoreCase(format.getExt())){
                        ff = format;
                        break;
                    }
                }
            }
        }
        if (ff == null){
            ff = FileFormat.JPG;
        }

        ImageFormat format = null;

        if (value.startsWith(ImageResizeType.BIG_SIZE.getCode())) {
            format = new ImageFormat(parseSize(value.substring(1)), ImageResizeType.BIG_SIZE, ff);
        } else if (value.startsWith(ImageResizeType.SQUARE.getCode())) {

            format = new ImageFormat(parseSize(value.substring(1)), ImageResizeType.SQUARE, ff);
        } else if (value.endsWith(ImageResizeType.WIDTH.getCode())) {

            format = new ImageFormat(parseSize(value.substring(0, value.length()-1)), ImageResizeType.WIDTH, ff);
        } else if (value.startsWith(ImageResizeType.RECTANGLE_CENTER.getCode())) {

            int delimiterIndex = value.indexOf('x');
            if ( delimiterIndex < 0) {
                throw new RuntimeException("Wrong format for RECTANGLE_CENTER type.");
            }

            format = new ImageFormat(parseSize(value.substring(2, delimiterIndex)), parseSize(value.substring(delimiterIndex + 1)), ImageResizeType.RECTANGLE_CENTER, ff);
        }

        return format;
    }

    private static int parseSize(String value){
        int tmp = Integer.parseInt(value);
        if (tmp < MIN_DIMENSION){
            tmp = MIN_DIMENSION;
        }else if (tmp > MAX_DIMENSION){
            tmp = MAX_DIMENSION;
        }
        return tmp;
    }

    public String save(boolean ext) {
        StringBuilder sb = new StringBuilder();

        switch (getResizeType()) {
            case BIG_SIZE:
                sb.append(ImageResizeType.BIG_SIZE.getCode()).append(getSize());
                break;
            case SQUARE:
                sb.append(ImageResizeType.SQUARE.getCode()).append(getSize());
                break;
            case WIDTH:
                sb.append(getSize()).append(ImageResizeType.WIDTH.getCode());
                break;
            case RECTANGLE_CENTER:
                sb.append(ImageResizeType.RECTANGLE_CENTER.getCode()).append(getSize()).append('x').append(getSize2());
                break;
        }
        if (ext && format != null){
            sb.append(".").append(format.getExt());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ImageFormat{" +
                "size=" + size +
                "size2=" + size2 +
                ", resizeType=" + resizeType +
                ", format=" + format +
                '}';
    }
}
