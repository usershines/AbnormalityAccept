package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.FileService;
import com.abnormality.abnormalityaccept.util.AFileUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件服务实现类，用于处理与 MinIO 对象存储交互的文件上传和 URL 获取操作。
 *
 * <p>该类实现了 FileService 接口，封装了文件上传至 MinIO 及生成预签名访问 URL 的逻辑。</p>
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * MinIO 客户端实例，用于执行对象存储操作。
     */
    @Autowired
    private MinioClient minioClient;

    /**
     * 存储桶名称，从配置文件中注入，用于指定上传文件的目标存储位置。
     */
    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 预签名 URL 的过期时间（单位：秒），默认为 30 分钟。
     */
    private final Integer expSecs = 30 * 60;

    /**
     * 获取指定文件的预签名访问 URL。
     *
     * <p>通过 MinIO 客户端生成一个带有访问权限的临时 URL，允许在有效期内下载该文件。</p>
     *
     * @param filename 文件名，用于构建访问路径
     * @return 生成的预签名 URL 字符串
     * @throws ServiceException 如果生成 URL 过程中发生异常，则抛出业务异常
     */
    @Override
    public String getPublicUrl(String filename) {
//        try {
//            // 调用 MinIO API 生成 GET 方法可访问的预签名 URL
//            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                    .bucket(bucketName)
//                    .object(filename)
//                    .method(Method.GET)
//                    .expiry(expSecs)
//                    .build());
//        } catch (Exception e) {
//            // 记录日志并抛出异常，避免调用方无法感知错误
//            log.error("获取上传文件URL失败：{}", e.getMessage());
//            return "";
//        }
        if(StrUtil.isBlank(filename)){
            return "";
        }
        return endpoint + "/" + bucketName + "/" + filename;
    }

    /**
     * 将字节数组上传至 MinIO，并返回目标文件名。
     *
     * <p>该方法的主要流程如下：</p>
     * <ul>
     *   <li>将传入的字节数组写入本地临时文件。</li>
     *   <li>使用 MinIO 客户端上传该临时文件到指定存储桶。</li>
     *   <li>上传成功后删除临时文件，并返回生成的文件名。</li>
     * </ul>
     *
     * @param fileName 原始文件名，用于构造临时文件及最终存储名
     * @param bytes    待上传的文件内容，以字节数组形式提供
     * @return 上传后的文件名（唯一随机前缀 + 原始文件名）
     * @throws ServiceException 如果上传过程中发生异常，则抛出业务异常
     */
    @Override
    public String upload(String fileName, byte[] bytes) {
        // 创建临时文件用于上传
        String tempFilePath = AFileUtil.createTempFile(fileName, bytes);
        // 生成唯一文件名，防止重名冲突
        String targetFileName = RandomUtil.randomString(5) + fileName;

        try {
            // 执行 MinIO 文件上传操作
            ObjectWriteResponse wres = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetFileName)
                            .filename(tempFilePath)
                            .build());

            // 检查上传结果是否成功
            if (wres.etag() == null) {
                throw new ServiceException("上传文件失败");
            }

            // 删除本地临时文件
            File t = new File(tempFilePath);
            if (t.exists()) {
                t.delete();
            }

            // 返回上传后的文件名
            return targetFileName;
        } catch (Exception e) {
            // 记录日志并删除临时文件，同时抛出异常
            log.error("上传文件失败：{}", e.getMessage());
            File t = new File(tempFilePath);
            if (t.exists()) {
                t.delete();
            }
            throw new ServiceException("上传文件失败");
        }
    }

    @Override
    public boolean exist(String fileName) {
        try {
            // 尝试获取对象的元信息，如果不存在会抛出异常
            minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            // 未抛异常，说明文件存在
            return true;
        } catch (Exception e) {
            // 如果是对象不存在的异常，则返回 false
            if (e.getMessage() != null && e.getMessage().contains("The specified key does not exist")) {
                return false;
            }
            // 其他异常建议记录日志或处理
            log.warn("检查文件是否存在时发生异常: {}", e.getMessage());
            return false;
        }
    }


    @Override
    public Abnormality completeImageUrl(Abnormality abnormality) {
        Abnormality abnormalityVo = new Abnormality();
        abnormalityVo.setId(abnormality.getId());
        abnormalityVo.setName(abnormality.getName());
        abnormalityVo.setLevel(abnormality.getLevel());
        abnormalityVo.setDescription(abnormality.getDescription());
        abnormalityVo.setManageMethod(abnormality.getManageMethod());
        abnormalityVo.setNotes(abnormality.getNotes());
        abnormalityVo.setFacilityId(abnormality.getFacilityId());
        abnormalityVo.setImgName(getPublicUrl(abnormality.getImgName()));
//        abnormalityVo.setPageNum(abnormality.getPageNum());
//        abnormalityVo.setPageSize(abnormality.getPageSize());
        return abnormalityVo;
    }

    @Override
    public List<Abnormality> completeImageUrl(List<Abnormality> abnormalityList) {
        List<Abnormality> abnormalityVoList = new ArrayList<>();
        for(Abnormality abnormality:abnormalityList){
            abnormalityVoList.add(completeImageUrl(abnormality));
        }
        return abnormalityVoList;
    }

    @Override
    public String test() {
        try{
            List<String> results=new ArrayList<>();
//            return JSONUtil.toJsonStr(minioClient.listBuckets());
            Object result=minioClient.getBucketEncryption(GetBucketEncryptionArgs.builder().bucket(bucketName).build());
            results.add(JSONUtil.toJsonStr(result));
            result=minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
            results.add((String) result);
            log.info("result:{}",results);
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                log.info(bucket.creationDate() + ", " + bucket.name());
            }
            return JSONUtil.toJsonStr(results);
        } catch (Exception e) {

            throw new ServiceException(Code.ERROR, "获取数据失败",e);
        }

    }
}
