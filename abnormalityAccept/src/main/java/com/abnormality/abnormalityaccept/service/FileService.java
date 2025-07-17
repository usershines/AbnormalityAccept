package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Abnormality;

import java.util.List;

public interface FileService {
    String getPublicUrl(String filename);
    String upload(String fileName, byte[] bytes);
    boolean exist(String fileName);
    Abnormality completeImageUrl(Abnormality abnormality);
    List<Abnormality> completeImageUrl(List<Abnormality> abnormalityList);
}
