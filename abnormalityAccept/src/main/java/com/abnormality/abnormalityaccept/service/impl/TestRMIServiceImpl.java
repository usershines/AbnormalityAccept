package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.dto.request.TestRequest;
import com.abnormality.abnormalityaccept.dto.response.TestResponse;
import com.abnormality.abnormalityaccept.service.TestRMIService;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI服务实现类
 */
@Service
public class TestRMIServiceImpl extends UnicastRemoteObject implements TestRMIService {

    public TestRMIServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public TestResponse test(TestRequest req) throws RemoteException {
        TestResponse res = new TestResponse();
        res.setName(req.getName());
        res.setAge(req.getAge() + 10); // 通过RMI调用时年龄增加10岁以示区别
        res.setMsg("RMI远程调用成功");
        return res;
    }
}
