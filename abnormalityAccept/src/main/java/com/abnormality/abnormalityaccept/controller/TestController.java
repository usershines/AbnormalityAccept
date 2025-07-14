package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller以及Result使用示例
 */
@RestController
@CrossOrigin
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;
    @PostMapping("/testPost")
    public Result<TestResponse> testPost(@RequestBody TestRequest req) {
        return Result.ok(testService.test(req));
    }
}
