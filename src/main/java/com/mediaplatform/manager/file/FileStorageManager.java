package com.mediaplatform.manager.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.mediaplatform.jsf.fileupload.UploadedFile;
import com.mediaplatform.manager.ConfigBean;
import com.mediaplatform.model.*;
import com.mediaplatform.util.FileFormat;
import com.mediaplatform.util.ImageConverter;
import com.mediaplatform.util.ImageFormat;
import org.jboss.solder.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;

/**
 * User: timur
 * Date: 11/29/12
 * Time: 4:37 PM
 */
@ApplicationScoped
@Named
public class FileStorageManager implements Serializable{
    @Inject
    private ConfigBean configBean;

    @Inject
    private ImageConverter imageConverter;

    @Inject
    protected Logger log;

    private File fileStorageDir;

    @Inject
    private FacesContext facesContext;


    @PostConstruct
    public void init() {
        String fileStoragePath = configBean.getFileStoragePath();
        fileStorageDir = new File(fileStoragePath);
        if (!fileStorageDir.exists()) fileStorageDir.mkdir();
    }

    public FileEntry saveFile(ParentRef parentRef, UploadedFile file, DataType dataType) {
        String originalName = file.getName();
        String fileEntryName = originalName;
        EntityType entityType = parentRef.getEntityType();
        File outDir = getDestDir(dataType, entityType);

        String folderPath = StringUtils.substringAfter(outDir.getAbsolutePath(), fileStorageDir.getAbsolutePath());
        FileFormat format = FileFormat.getFormat(file.getFullName());

        String name = fileEntryName + (StringUtils.isNotBlank(file.getExtension()) ? "." + file.getExtension() : "");
        FileEntry fileEntry = new FileEntry(fileEntryName, name, folderPath, format, originalName, file.getLength(), dataType, parentRef);

        //saving original image
        InputStream input = null;
        FileOutputStream output = null;
        try {
            if(file.getFile() == null){
                input = new ByteArrayInputStream(file.getData());
            }else{
                input = new FileInputStream(file.getFile());
            }

            output = new FileOutputStream(new File(outDir, name));
            IOUtils.copy(input, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            if(file.getFile() != null) file.getFile().delete();
        }

        return fileEntry;
    }

    public File getImageFile(FileEntry fileEntry, ImageFormat format) {

        File dir = getDestDir(fileEntry.getDataType(), fileEntry.getParentRef().getEntityType());
        File origFile = new File(dir, fileEntry.getFullName());
        String resizedFileName = fileEntry.getName() + format.getResizeType().getCode() + format.getSize() + "." + fileEntry.getFormat().getExt();
        File resizedFile = new File(dir, resizedFileName);
        if (resizedFile.exists()) return resizedFile;
        if (!origFile.exists()){
            log.error("File not exists " + origFile.getAbsolutePath());
            origFile = getDefaultImage();
        }
        imageConverter.convert(origFile, resizedFile, format);
        return resizedFile;
    }

    public File getDefaultImage() {
        return new File(facesContext.getExternalContext().getRealPath("images/default2.jpg"));
    }

    public String getImageFileUrl(FileEntry fileEntry, ImageFormat format) {
        File file = getImageFile(fileEntry, format);
        String result = StringUtils.substringAfter(file.getAbsolutePath(), fileStorageDir.getAbsolutePath());
        return result;
    }

    public String getMediaFileUrl(FileEntry fileEntry) {
        return getMediaFileUrl(fileEntry, false);
    }

    public String getMediaFileUrl(FileEntry fileEntry, boolean absPath) {
        File dir = getDestDir(fileEntry.getDataType(), fileEntry.getParentRef().getEntityType());
        File origFile = new File(dir, fileEntry.getFullName());
        if(absPath) return origFile.getAbsolutePath();
        String result = StringUtils.substringAfter(origFile.getAbsolutePath(), fileStorageDir.getAbsolutePath());
        return result;
    }

    private File getDestDir(DataType dataType, EntityType entityType) {
        File dataTypeDir = new File(fileStorageDir, dataType.name().toLowerCase());
        if (!dataTypeDir.exists()) {
            dataTypeDir.mkdir();
        }
        File entityTypeDir = new File(dataTypeDir, entityType.name().toLowerCase());
        if (!entityTypeDir.exists()) {
            entityTypeDir.mkdir();
        }
        return entityTypeDir;
    }

    public File getFileStorageDir() {
        return fileStorageDir;
    }
}
