package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.github.pagehelper.PageInfo;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileService {
    String getPublicUrl(String filename);
    String upload(String fileName, byte[] bytes);
    boolean exist(String fileName);
    Abnormality completeImageUrl(Abnormality abnormality);
    List<Abnormality> completeImageUrl(List<Abnormality> abnormalityList);
    PageInfo<Abnormality> completeImageUrl(PageInfo<Abnormality> abnormalityList);
    String test();
}
