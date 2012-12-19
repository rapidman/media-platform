package com.mediaplatform.model;

import com.mediaplatform.util.FileFormat;
import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "files")
@Veto
public class FileEntry implements Serializable {
    private Long id;

    private String name;

    private String fullName;

    private FileFormat format;

    private String originalName;

    private long size;

    private String metadata; //некоторые метаданные

    private DataType dataType;

    private ParentRef parentRef;

    private String folderPath;

    public FileEntry() {
    }

    public FileEntry(String name,
                     String fullName,
                     String folderPath,
                     FileFormat format,
                     String originalName,
                     long size,
                     DataType dataType,
                     ParentRef parentRef) {
        this.name = name;
        this.fullName = fullName;
        this.folderPath = folderPath;
        this.format = format;
        this.originalName = originalName;
        this.size = size;
        this.dataType = dataType;
        this.parentRef = parentRef;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && format != null) {
            if (name.toLowerCase().endsWith("." + format.getExt())) {
                name = name.substring(0, name.length() - 1 - format.getExt().length());
            }
        }
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public FileFormat getFormat() {
        return format;
    }

    public void setFormat(FileFormat format) {
        this.format = format;
    }

    @Column(name = "original_name")
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Lob
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @NotNull
    @Embedded
    public ParentRef getParentRef() {
        return parentRef;
    }

    public void setParentRef(ParentRef parentRef) {
        this.parentRef = parentRef;
    }

    @NotNull
    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public String toString() {
        return "FileEntry{" +
                "name='" + name + '\'' +
                '}';
    }
}
