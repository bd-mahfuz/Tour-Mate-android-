package com.example.mahfuz.tourmate.model;

import java.io.Serializable;

public class Moment implements Serializable{
    private String id;
    private String fileName;
    private String formatName;
    private String photoPath;
    private long date;

    public Moment() {
        //required for firebase
    }

    public Moment(String id, String fileName, String formatName, long date) {
        this.id = id;
        this.fileName = fileName;
        this.formatName = formatName;
        this.date = date;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
