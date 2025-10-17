package com.abnormality.abnormalityaccept.util;

import com.aliyun.facebody20191230.models.CreateFaceDbResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;

public class CreateFaceDb {
    public static com.aliyun.facebody20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放 AccessKeyId、AccessKeySecret、endpoint等配置
         */
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        return new com.aliyun.facebody20191230.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        // 创建AccessKey ID和AccessKey Secret，请参考https://help.aliyun.com/document_detail/175144.html
        // 如果您使用的是RAM用户的AccessKey，还需要为子账号授予权限AliyunVIAPIFullAccess，请参考https://help.aliyun.com/document_detail/145025.html
        // 从环境变量读取配置的AccessKey ID和AccessKey Secret。运行代码示例前必须先配置环境变量。
        String accessKeyId = "";
        String accessKeySecret = "";
        com.aliyun.facebody20191230.Client client = CreateFaceDb.createClient(accessKeyId, accessKeySecret);
        com.aliyun.facebody20191230.models.CreateFaceDbRequest createFaceDbRequest = new com.aliyun.facebody20191230.models.CreateFaceDbRequest()
                .setName("Face1");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            CreateFaceDbResponse createFaceDbResponse = client.createFaceDbWithOptions(createFaceDbRequest, runtime);
            // 获取整体结果
            System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(createFaceDbResponse)));
        } catch (TeaException teaException) {
            // 获取整体报错信息
            System.out.println((teaException.getMessage()));
            // 获取单个字段
            System.out.println(teaException.getCode());
        }
    }
}