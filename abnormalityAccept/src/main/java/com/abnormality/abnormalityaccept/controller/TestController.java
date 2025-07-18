package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.service.FileService;
import com.abnormality.abnormalityaccept.service.TestService;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

/**
 * 测试
 */
@RestController
@CrossOrigin
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FileService fileService;
    @GetMapping("/test")
    public Result<String> testDataSource() {
        // 打印当前数据源
        return Result.ok(fileService.test());

    }

    @Autowired
    private TestService testService;
    @PostMapping("/testPost")
    public Result<TestResponse> testPost(@RequestBody TestRequest req) {
        return Result.ok(testService.test(req));
    }
}
