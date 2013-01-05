package com.mediaplatform.jsf.fileupload;

import org.apache.commons.io.IOUtils;
import org.jboss.solder.core.Veto;
import org.richfaces.event.FileUploadEvent;

import java.io.*;
import java.util.ArrayList;

@Veto
public class FileUploadBean implements Serializable {

    private ArrayList<UploadedFile> files = new ArrayList<UploadedFile>();

    public void paint(OutputStream stream, Object object) throws IOException {
        UploadedFile uploadedFile = getFiles().get((Integer) object);
        if(uploadedFile.getFile() == null){
            stream.write(uploadedFile.getData());
        }else{
            FileInputStream input = new FileInputStream(uploadedFile.getFile());
            try{
                IOUtils.copy(input, stream);
            }finally {
                IOUtils.closeQuietly(input);
            }
        }
        stream.close();
    }

    private void clear() {
        files.clear();
    }

    public void listener(FileUploadEvent event) throws Exception {
        clear();
        org.richfaces.model.UploadedFile item = event.getUploadedFile();
        UploadedFile file = new UploadedFile();
        File tmpFile = File.createTempFile(item.getName(), "");
        tmpFile.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(tmpFile);
        try{
            IOUtils.copy(item.getInputStream(), fos);
            file.setFile(tmpFile);
        }finally {
            IOUtils.closeQuietly(fos);
        }
//        file.setLength(item.getData().length);
        file.setLength(tmpFile.length());
        file.setName(item.getName());
//        file.setData(item.getData());
        files.add(file);
    }

    public String clearUploadData() {
        clear();
        return null;
    }

    public int getSize() {
        if (getFiles().size() > 0) {
            return getFiles().size();
        } else {
            return 0;
        }
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public ArrayList<UploadedFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<UploadedFile> files) {
        this.files = files;
    }

}
