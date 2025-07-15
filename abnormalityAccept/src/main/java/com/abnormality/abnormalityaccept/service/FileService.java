package com.abnormality.abnormalityaccept.service;

public interface FileService {
    String getPublicUrl(String filename);
    String upload(String fileName, byte[] bytes);

}
