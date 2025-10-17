package com.abnormality.abnormalityaccept.service;

import java.io.IOException;

public interface FaceRecogService {
    String faceRegister(byte[] imageBase64, String userId) throws IOException, Exception;
    String faceRecognize(byte[] imageBase64) throws Exception;
}
