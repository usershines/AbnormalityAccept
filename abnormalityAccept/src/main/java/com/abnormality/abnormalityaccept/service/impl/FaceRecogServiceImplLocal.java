package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.service.FaceRecogService;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FaceRecogServiceImplLocal implements FaceRecogService {

    private String endpoint = "http://127.0.0.1";

    @Override
    public String faceRegister(byte[] imageBase64, String userId) throws IOException, Exception {
        // 创建连接URL
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(imageBase64, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(endpoint + "/face/register")
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
                .url(endpoint + "/face/rec")
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
}
