package com.abnormality.abnormalityaccept.config;

import com.abnormality.abnormalityaccept.service.TestRMIService;
import com.abnormality.abnormalityaccept.service.impl.TestRMIServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI配置类
 */
@Configuration
public class RMIConfig {

    @Value("${rmi.port:1099}")
    private int rmiPort;

    @Value("${rmi.service.name:testService}")
    private String serviceName;

    /**
     * 创建并导出RMI服务
     */
    @Bean
    public Registry rmiRegistry() {
        try {
            // 创建RMI注册表
            Registry registry = LocateRegistry.createRegistry(rmiPort);

            // 创建服务实现
            TestRMIService service = new TestRMIServiceImpl();

            // 绑定服务到注册表 (TestRMIServiceImpl继承了UnicastRemoteObject，已经自动导出)
            registry.bind(serviceName, service);

            System.out.println("RMI服务启动成功，端口: " + rmiPort + ", 服务名: " + serviceName);
            return registry;
        } catch (Exception e) {
            System.err.println("RMI服务启动失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
