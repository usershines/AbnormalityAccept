package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI远程服务接口
 */
public interface TestRMIService extends Remote {
    /**
     * 测试RMI远程调用方法
     * @param req 请求参数
     * @return 响应结果
     * @throws RemoteException 远程调用异常
     */
    TestResponse test(TestRequest req) throws RemoteException;
}
