package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.FileService;
import com.abnormality.abnormalityaccept.util.AFileUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    private final Integer expSecs=30*60;

    @Override
    public String getPublicUrl(String filename) {
        try {
            // 获取预签名URL
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(filename).method(Method.GET).expiry(expSecs).build());
        } catch (Exception e) {
            log.error("获取上传文件URL失败：{}", e.getMessage());
            throw new ServiceException("获取上传文件URL失败");
        }
    }

    @Override
    public String upload(String fileName, byte[] bytes) {
        String tempFilePath = AFileUtil.createTempFile(fileName,bytes);
        String targetFileName= RandomUtil.randomString(5)+fileName;
        try {
            // 上传文件到MinIO存储桶
            ObjectWriteResponse wres = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetFileName)
                            .filename(tempFilePath)
                            .build());

            // 检查上传结果是否有效
            if (wres.etag() == null) {
                throw new ServiceException("上传文件失败");
            }
            // 删除临时文件
            File t = new File(tempFilePath);
            if (t.exists()) {
                t.delete();
            }
            return targetFileName;
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
            // 删除临时文件
            File t = new File(tempFilePath);
            if (t.exists()) {
                t.delete();
            }
            throw new ServiceException("上传文件失败");
        }
    }




}
