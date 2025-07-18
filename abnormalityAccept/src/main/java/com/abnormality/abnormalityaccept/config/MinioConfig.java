package com.abnormality.abnormalityaccept.config;

import cn.hutool.json.JSONUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类，用于初始化 MinioClient 并在启动时检查或创建指定的存储桶。
 *
 * <p>该类通过 Spring 的 @Configuration 注解标识为配置类，并利用 Lombok 提供的日志记录器进行日志输出。</p>
 */
@Configuration
@Slf4j
public class MinioConfig {

    /**
     * MinIO 服务端点地址，从配置文件中注入。
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * MinIO 用户名，用于认证，从配置文件中注入。
     */
    @Value("${minio.username}")
    private String username;

    /**
     * MinIO 密码，用于认证，从配置文件中注入。
     */
    @Value("${minio.password}")
    private String password;

    /**
     * 存储桶名称，用于初始化 MinIO 存储桶，从配置文件中注入。
     */
    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 初始化并配置 MinioClient Bean。
     *
     * <p>此方法会根据配置信息连接 MinIO 服务器，并检查指定的存储桶是否存在：</p>
     * <ul>
     *   <li>如果存储桶不存在，则自动创建一个新的存储桶。</li>
     *   <li>如果过程中发生异常，将记录错误日志并终止程序。</li>
     * </ul>
     *
     * @return 已配置完成的 MinioClient 实例
     */
    @Bean
    public MinioClient minioClient() {
        // 构建 MinioClient 实例
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(username, password).build();

        try {
            // 检查存储桶是否存在
            boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!exist) {
                // 如果存储桶不存在，则创建新的存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            log.info("存储桶已存在");
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(getBucketPolicy()).build());
        } catch (Exception e) {
            // 记录初始化失败的日志信息，并退出程序
            log.error("初始化MinIO失败：{}", e.getMessage());
            System.exit(1);
        }

        // 记录初始化成功的日志信息
        log.info("初始化MinIO成功");

        return minioClient;
    }

    private String getBucketPolicy() {
//        StringBuilder builder=new StringBuilder();
//        builder.append("{\n" +
//                "  \"Version\": \"2012-10-17\",\n" +
//                "  \"Statement\": [\n" +
//                "    {\n" +
//                "      \"Effect\": \"Allow\",\n" +
//                "      \"Action\": [\n" +
////                "                \"s3:ListAllMyBuckets\",\n" +
//                "                \"s3:ListBucket\",\n" +
//                "                \"s3:GetBucketLocation\",\n" +
//                "                \"s3:GetObject\",\n" +
////                "                \"s3:PutObject\",\n" +
////                "                \"s3:DeleteObject\"\n" +
//                "      ],\n" +
//                "      \"Principal\":\"*\",\n" +
//                "      \"Resource\": [\n" +
//                "        \"arn:aws:s3:::"+bucketName+"/*\"\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}");
            MinIoBucketPolicy policy=MinIoBucketPolicy.readOnly(bucketName);
//            log.info("bucketPolicy:{}",JSONUtil.toJsonStr(policy));
//            return JSONUtil.toJsonStr(policy);
        log.info("bucketPolicy:{}",policy.toString());
        return policy.toString();
    }
}
