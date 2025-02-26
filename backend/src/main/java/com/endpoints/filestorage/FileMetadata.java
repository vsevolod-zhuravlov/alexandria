package com.endpoints.filestorage;


public class FileMetadata {
    public String fileId;
    public String projectAddress;
    public String taskAddress;
    public String studentAddress;
    public String fileExtension;

    public FileMetadata() {
    }

    public FileMetadata(String fileId, String projectAddress, String taskAddress, String studentAddress, String fileExtension) {
        this.fileId = fileId;
        this.projectAddress = projectAddress;
        this.taskAddress = taskAddress;
        this.studentAddress = studentAddress;
        this.fileExtension = fileExtension;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public String getTaskAddress() {
        return taskAddress;
    }

    public void setTaskAddress(String taskAddress) {
        this.taskAddress = taskAddress;
    }

    public String getStudentAddress() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}