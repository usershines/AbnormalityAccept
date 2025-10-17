package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.FaceRecogService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class FaceRecogServiceImplLocal implements FaceRecogService {

    private String endpoint = "http://127.0.0.1:8894";

    @Override
    public String faceRegister(byte[] imageBase64, String userId) throws IOException, Exception {
        // 创建连接URL
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", userId)
                .addFormDataPart("name", userId)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(imageBase64, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(endpoint + "/api/face/reg")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("HTTP request failed with code: " + response.code());
            }
        }
    }

    @Override
    public String faceRecognize(byte[] imageBase64) throws Exception {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(imageBase64, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(endpoint + "/api/face/identif")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONArray data = (JSONArray) JSONUtil.parseObj(response.body().string()).get("data");
                String id= ((JSONObject)(data.get(0))).get("name").toString();
                if (StrUtil.isBlank(id)){
                    throw new ServiceException(Code.NOT_FOUND, "未找到用户");
                }
                return id;
            } else {
                throw new IOException("HTTP request failed with code: " + response.code());
            }
        }
    }
}
