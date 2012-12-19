package com.mediaplatform.jsf.fileupload;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.Serializable;

public class UploadedFile implements Serializable {
    private static final long serialVersionUID = -8192553629588066292L;
    private String name;
    private long length;
    private byte[] data;
    private String extension = "";
    private File file;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String dot = ".";
        int extDot = name.lastIndexOf(dot);
        if (extDot > 0) {
            extension = name.substring(extDot + 1);
            this.name = StringUtils.substringBefore(name, dot);
            return;
        }
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getExtension() {
        return extension;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFullName(){
        return name + (extension.isEmpty() ? "" : "." + extension);
    }
}
