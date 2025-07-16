package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.service.TestService;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

/**
 * Controller以及Result使用示例
 */
@RestController
@CrossOrigin
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/testDataSource")
    public void testDataSource() {
        // 打印当前数据源
        System.out.println("当前数据源类型: " + dataSource.getClass().getName());

        // 动态数据源应该是 com.baomidou.dynamic.datasource.DynamicRoutingDataSource
        if (dataSource instanceof DynamicRoutingDataSource) {
            DynamicRoutingDataSource dynamicDataSource = (DynamicRoutingDataSource) dataSource;
            System.out.println("所有数据源: " + dynamicDataSource.getDataSources().keySet());
        }
    }

    @Autowired
    private TestService testService;
    @PostMapping("/testPost")
    public Result<TestResponse> testPost(@RequestBody TestRequest req) {
        return Result.ok(testService.test(req));
    }
}
