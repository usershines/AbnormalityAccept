package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {



    @Override
    public TestResponse test(TestRequest req) {
        TestResponse res = new TestResponse();
        res.setName(req.getName());
        res.setAge(req.getAge());
        res.setMsg("hello world");
        return res;
    }
}
