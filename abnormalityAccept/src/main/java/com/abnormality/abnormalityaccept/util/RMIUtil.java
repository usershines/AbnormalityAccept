package com.abnormality.abnormalityaccept.util;

import com.abnormality.abnormalityaccept.service.TestRMIService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI客户端工具类
 */
public class RMIUtil {

    /**
     * 获取远程服务代理
     * @param host 服务器地址
     * @param port 端口
     * @param serviceName 服务名称
     * @return 远程服务代理
     * @throws Exception 连接异常
     */
    public static TestRMIService getRemoteService(String host, int port, String serviceName) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (TestRMIService) registry.lookup(serviceName);
    }
}
