package com.abnormality.abnormalityaccept.aspect;

import cn.hutool.core.util.StrUtil;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.handler.AuthHandler;
import com.abnormality.abnormalityaccept.util.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 认证切面，用于在请求进入 Controller 层时进行 Token 认证。
 *
 * <p>该切面定义在所有 {@link com.abnormality.abnormalityaccept.controller controller} 包下的方法上执行，
 * 但排除带有 {@link com.abnormality.abnormalityaccept.annotation.AuthIgnore @AuthIgnore} 注解的方法。
 * 切面在目标方法执行前进行 Token 验证，确保用户已登录。</p>
 *
 * <p>若 Token 验证失败或未通过认证，将抛出 BaseException 异常，并记录相关日志。</p>
 */
@Aspect
@Component
@Slf4j
@Order(2)
public class AuthAspect {

    /**
     * Spring 应用上下文，用于获取 AuthHandler 实例（当前被 @Autowired 注入替代）。
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 认证处理器，用于执行 Token 的验证逻辑。
     */
    @Autowired
    private AuthHandler authHandler;

    /**
     * 定义切入点：匹配 controller 包下所有未被 @AuthIgnore 注解标注的方法。
     */
    @Pointcut("!@annotation(com.abnormality.abnormalityaccept.annotation.AuthIgnore) && execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void auth() {}

    /**
     * 在目标方法执行前进行 Token 认证检查。
     *
     * <p>该方法首先从 AopUtil 获取当前请求的 Token，然后调用 AuthHandler 的 verify 方法进行验证。
     * 如果验证通过，则继续执行后续逻辑；否则抛出 BaseException 异常。</p>
     *
     * <p>在整个验证过程中，会记录 Token 获取、验证耗时以及异常信息的日志。</p>
     *
     * @param joinPoint 切点对象，用于获取目标方法的上下文信息
     * @throws BaseException 如果 Token 验证失败，抛出未授权异常
     */
    @Before("auth()")
    public void checkAuth(JoinPoint joinPoint) {
        // 记录认证开始时间，用于计算处理耗时
        long startTime = System.currentTimeMillis();

        // 从上下文中获取 Token
        String token = AopUtil.getToken();
        log.info("登录切面获取token {}", token);

        log.info("token: " + token);

        try {
            // 使用注入的 AuthHandler 实例进行 Token 验证
            // boolean passed = authHandler.verify(token);
            authHandler.verify(token);
        } catch (Exception e) {
            // Token 验证失败，记录错误日志并抛出异常
            log.error("令牌验证失败", e);
            log.info("耗时{}", System.currentTimeMillis() - startTime);
            throw new BaseException(Code.NOT_AUTHORIZED, "登录令牌验证失败", e);
        }
    }
}
