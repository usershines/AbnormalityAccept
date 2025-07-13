package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;

public interface TestService {
    TestResponse test(TestRequest req);
}

