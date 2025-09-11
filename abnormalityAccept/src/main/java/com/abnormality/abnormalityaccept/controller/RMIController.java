package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.annotation.AuthIgnore;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.util.RMIUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;

/**
 * RMI测试控制器
 */
@RestController
@CrossOrigin
@RequestMapping("/rmi")
@Tag(name = "RMI远程调用测试")
public class RMIController {

    @Value("${rmi.port:1099}")
    private int rmiPort;

    @Value("${rmi.service.name:testService}")
    private String serviceName;

    @Value("${rmi.remote.host:localhost}")
    private String remoteHost;

    @AuthIgnore
    @PostMapping("/test")
    public Result<TestResponse> testRMI(@RequestBody TestRequest req) {
        try {
            // 获取本地RMI服务

            var remoteService = RMIUtil.getRemoteService(remoteHost, rmiPort, serviceName);

            // 调用远程方法
            TestResponse response = remoteService.test(req);

            return Result.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("RMI调用失败: " + e.getMessage());
        }
    }
}
