package com.abnormality.abnormalityaccept.aspect;

import cn.hutool.core.util.StrUtil;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.handler.AuthHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 日志切面类，用于统一处理控制器层的请求日志记录、权限验证和异常捕获。
 *
 * <p>该类通过 AOP 环绕通知对控制器方法进行拦截，实现了以下功能：</p>
 * <ul>
 *   <li>记录请求 URL、方法名、参数、耗时等信息。</li>
 *   <li>对非登录/注册接口进行 Token 鉴权。</li>
 *   <li>捕获业务异常并返回统一错误响应。</li>
 *   <li>发布异常日志事件供监听器处理。</li>
 * </ul>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * Spring 应用上下文对象，用于发布自定义事件（如异常日志事件）。
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 认证处理器实例，用于执行 Token 验证逻辑。
     */
    @Autowired
    private AuthHandler authHandler;

    /**
     * 定义切点，匹配控制器包下的所有方法。
     */
    @Pointcut("execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void log() {
        // 仅作为切点声明使用
    }

    /**
     * 环绕通知方法，用于处理控制器方法的整个执行过程。
     *
     * <p>主要完成以下任务：</p>
     * <ol>
     *   <li>记录请求开始时间，计算执行耗时。</li>
     *   <li>获取请求相关信息（URL、IP、User-Agent、参数等）。</li>
     *   <li>对除 login 和 register 外的所有方法执行 Token 验证。</li>
     *   <li>执行目标方法，并记录正常响应日志。</li>
     *   <li>捕获异常，记录异常信息并发布 ExceptionLogEvent。</li>
     *   <li>统一返回封装后的 Result 对象。</li>
     * </ol>
     *
     * @param joinPoint 连接点对象，包含被拦截方法的信息和参数
     * @return 返回封装后的 {@link Result} 响应对象
     */
    @Around("log()")
    public Result<?> controller(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes)
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String url = null;
        if (request != null) {
            url = request.getRequestURI();
        }
        String methodName = method.getName();

        // 获取请求头中的 Token 并进行鉴权，排除 login 和 register 方法
        List<String> nonAuthMethodName = Arrays.asList("login", "register");
        if (!nonAuthMethodName.contains(methodName)) {
            String token = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());
            }
            log.info("token: " + token);

            try {
                boolean passed = authHandler.verify(token);
            } catch (Exception e) {
                log.error("令牌验证失败", e);
                log.info("耗时{}", System.currentTimeMillis() - startTime);
                if (e instanceof BaseException) {
                    return Result.error(((BaseException) e).getCode().getCode(),
                            ((BaseException) e).getMsg(), ((BaseException) e).getMsgList());
                }
                return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
            }
        }

        // 执行目标方法并记录响应结果
        try {
            Result result = (Result) joinPoint.proceed();
            log.info("请求 URL: {}, 方法: {}, 参数：{}, 耗时: {}ms, 返回值: {}", url, methodName,
                    Arrays.toString(joinPoint.getArgs()), System.currentTimeMillis() - startTime, result);
            return result;
        } catch (Throwable e) {
            // 捕获异常并记录详细日志
            log.error("请求 URL: {}, 方法: {}, 发生异常: {}", url, methodName, e.getMessage());

            ExceptionLog exceptionLog = new ExceptionLog();
            exceptionLog.setExceptionInfo(Arrays.toString(e.getStackTrace()));

            // 获取本机主机名，标识发生异常的机器
            String machineId = "";
            try {
                machineId = InetAddress.getLocalHost().getHostName();
            } catch (Exception ex) {
                machineId = "unknown";
            }
            exceptionLog.setMachineId(machineId);

            // 设置异常消息
            if (e instanceof BaseException) {
                exceptionLog.setMessage(Arrays.toString(((BaseException) e).getMsgList().toArray()));
            } else {
                exceptionLog.setMessage(e.getMessage());
            }

            // 设置客户端 IP 地址
            exceptionLog.setIp(request.getRemoteAddr());
            // 设置方法名
            exceptionLog.setMethod(method.getName());
            // 设置请求 URL
            exceptionLog.setUrl(request.getRequestURI());
            // 设置 User-Agent
            exceptionLog.setUserAgent(request.getHeader("User-Agent"));
            // 设置请求参数
            Object[] args = joinPoint.getArgs();
            exceptionLog.setParams(Arrays.toString(args));
            // 设置用户名（远程用户）
            exceptionLog.setUsername(request.getRemoteUser());

            // 发布异常日志事件
            applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));

            // 输出日志内容便于调试
            log.info(exceptionLog.toString());
            log.error(exceptionLog.getExceptionInfo());
            log.error(exceptionLog.getMessage());
            e.printStackTrace();

            // 返回统一异常响应
            if (e instanceof BaseException) {
                return Result.error(((BaseException) e).getCode().getCode(),
                        ((BaseException) e).getCode().getMsg(), e.getMessage());
            } else {
                return Result.error(e.getMessage());
            }
        } finally {
            // 记录最终执行耗时
            long endTime = System.currentTimeMillis();
            log.info("耗时：" + (endTime - startTime));
        }
    }
}
