package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.service.FaceRecogService;
import com.aliyun.facebody20191230.models.AddFaceEntityResponse;
import com.aliyun.facebody20191230.models.AddFaceResponse;
import com.aliyun.facebody20191230.models.SearchFaceAdvanceRequest;
import com.aliyun.facebody20191230.models.SearchFaceResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import  com.aliyun.facebody20191230.Client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
public class FaceRecogServiceImpl implements FaceRecogService {


    private String targetUrl="http://127.0.0.1:5678";

    private static String dbName="Face1";

    private static String accessKeyId = "";
    private static String accessKeySecret = "";
    @Override
    public String faceRegister(byte[] image, String userId) throws Exception {
        InputStream inputStream =new ByteArrayInputStream(image);
        com.aliyun.facebody20191230.Client client = createClient(accessKeyId, accessKeySecret);
        if (!isEntityExist(userId)) {
            addFaceEntity(userId);
        }
        com.aliyun.facebody20191230.models.AddFaceAdvanceRequest addFaceRequest = new com.aliyun.facebody20191230.models.AddFaceAdvanceRequest()
                .setDbName(dbName)
                .setImageUrlObject(inputStream)
                .setEntityId(userId);

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        AddFaceResponse addFaceResponse=new AddFaceResponse();
        try {
            // 复制代码运行请自行打印 API 的返回值
            addFaceResponse = client.addFaceAdvance(addFaceRequest, runtime);
            // 获取整体结果
            System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(addFaceResponse)));
            // 获取单个字段
            System.out.println(addFaceResponse.getBody().getData().getFaceId());
        } catch (TeaException teaException) {
            // 获取整体报错信息
            System.out.println(teaException.getMessage());
            // 获取单个字段
            System.out.println(teaException.getCode());
            throw teaException;
        }
        return JSONUtil.toJsonStr(addFaceResponse.getBody().getData());
    }

    @Override
    public String faceRecognize(byte[] image) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret);
        // 场景一，使用本地文件
        InputStream inputStream =new ByteArrayInputStream(image);
        // 场景二，使用任意可访问的url
//        URL url = new URL("http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/SearchFace/SearchFace1.png");
//        InputStream inputStream = url.openConnection().getInputStream();
        SearchFaceAdvanceRequest searchFaceAdvanceRequest = new SearchFaceAdvanceRequest()
                .setDbName(dbName)
                .setLimit(1)
                .setImageUrlObject(inputStream);
        RuntimeOptions runtime = new RuntimeOptions();
        SearchFaceResponse searchFaceResponse = new SearchFaceResponse();
        try {
            // 复制代码运行请自行打印 API 的返回值
            searchFaceResponse = client.searchFaceAdvance(searchFaceAdvanceRequest, runtime);
            // 获取整体结果
            System.out.println(Common.toJSONString(TeaModel.buildMap(searchFaceResponse)));
            // 获取单个字段：Confidence转换后的置信度
            System.out.println(searchFaceResponse.getBody().getData().getMatchList().iterator().next().getFaceItems().iterator().next().getConfidence());
        } catch (TeaException teaException) {
            // 获取整体报错信息
            System.out.println(Common.toJSONString(teaException));
            // 获取单个字段
            System.out.println(teaException.getCode());
            throw teaException;
        }

        return searchFaceResponse.getBody().getData().getMatchList().get(0).getFaceItems().get(0).getEntityId();
    }

    public static boolean isEntityExist(String userId) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret);
        com.aliyun.facebody20191230.models.GetFaceEntityRequest getFaceEntityRequest = new com.aliyun.facebody20191230.models.GetFaceEntityRequest()
                .setDbName(dbName).setEntityId(userId);
        RuntimeOptions runtime = new RuntimeOptions();
        com.aliyun.facebody20191230.models.GetFaceEntityResponse getFaceEntityResponse = client.getFaceEntity(getFaceEntityRequest);
        FaceRecogServiceImpl.log.info(JSONUtil.toJsonStr(getFaceEntityResponse.getBody()));
        return getFaceEntityResponse.getBody().getData() != null;
    }

    public static String addFaceEntity(String userId) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret);
        com.aliyun.facebody20191230.models.AddFaceEntityRequest addFaceEntityRequest = new com.aliyun.facebody20191230.models.AddFaceEntityRequest()
                .setDbName(dbName)
                .setEntityId(userId);
        AddFaceEntityResponse addFaceEntityResponse =client.addFaceEntity(addFaceEntityRequest);
        return userId;
    }



    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放 AccessKeyId、AccessKeySecret、endpoint等配置
         */
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        return new Client(config);
    }

    public static void main(String[] args_) throws Exception {
        isEntityExist("42");
    }

}



