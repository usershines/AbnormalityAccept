package com.abnormality.abnormalityaccept.aspect;

import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.service.UserService;
import com.abnormality.abnormalityaccept.util.AopUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 权限控制切面，用于在方法执行前进行权限校验。
 *
 * <p>该切面通过解析方法上的 Level 注解，结合当前用户的 Token 提取用户信息，
 * 并根据配置的权限级别（允许权限、最小权限、最大权限）判断是否允许执行目标方法。</p>
 *
 * <p>若权限校验失败，将抛出 BaseException 异常，并记录相应的日志信息。</p>
 */
@Slf4j
@Aspect
@Component
@Order(3)
public class PermissionAspect {

    /**
     * 用户服务实例，用于根据用户名查询用户信息。
     */
    @Autowired
    private UserService userService;

    /**
     * 定义切入点，匹配所有标注了 com.abnormality.abnormalityaccept.annotation.Level 的方法。
     */
    @Pointcut("@annotation(com.abnormality.abnormalityaccept.annotation.Level)")
    public void permission() {}

    /**
     * 在目标方法执行前进行权限检查。
     *
     * <p>该方法首先从 AopUtil 获取当前请求的 Token，解析出 JwtPayload 信息，
     * 然后根据当前方法上的 Level 注解提取权限规则。接着查询当前用户的详细信息，
     * 根据配置的 allowLevel、minLevel 和 maxLevel 进行权限比对：</p>
     *
     * <ul>
     *   <li>如果设置了 allowLevel 且用户权限不在允许列表中，则抛出权限异常</li>
     *   <li>如果设置了 minLevel 且用户权限小于最小权限，则抛出权限不足异常</li>
     *   <li>如果设置了 maxLevel 且用户权限大于最大权限，则抛出权限过高异常</li>
     * </ul>
     *
     * <p>若权限校验通过，则输出成功日志。</p>
     *
     * @param joinPoint 切点对象，用于获取目标方法和方法签名等信息
     * @throws BaseException 如果权限校验失败，抛出对应的异常
     */
    @Before("permission()")
    public void checkPermission(JoinPoint joinPoint) {
        // 从上下文中获取 Token
        String token = AopUtil.getToken();
        log.info("权限检查切面获取token{}", token);

        // 获取目标方法的 MethodSignature 和 Method 对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的 Level 注解，提取权限配置
        Level levelAnnotation = method.getAnnotation(Level.class);
        int[] allowLevel = levelAnnotation.allowLevel();
        int minLevel = levelAnnotation.minLevel();
        int maxLevel = levelAnnotation.maxLevel();

        // 解析 Token 中的用户信息
        JwtPayload payload = JwtPayload.fromToken(token);
        User user = userService.findUserByName(payload.getUsername());

        // 检查是否在允许的权限列表内
        if (allowLevel.length > 0) {
            if (!ArrayUtils.contains(allowLevel, user.getLevel())) {
                log.info("用户{}执行了{}方法,权限鉴定失败，允许权限{}，用户权限{}", user.getUsername(), method, allowLevel, user.getLevel());
                throw new BaseException(Code.FORBIDDEN, "权限未在允许范围内");
            }
        }

        // 检查是否满足最低权限要求
        if (minLevel != -1) {
            if (user.getLevel() < minLevel) {
                log.info("用户{}执行了{}方法,权限鉴定失败，最小权限{}，用户权限{}", user.getUsername(), method, minLevel, user.getLevel());
                throw new BaseException(Code.FORBIDDEN, "权限不足");
            }
        }

        // 检查是否超过最高权限限制
        if (maxLevel != -1) {
            if (user.getLevel() > maxLevel) {
                log.info("用户{}执行了{}方法,权限鉴定失败，最大权限{}，用户权限{}", user.getUsername(), method, maxLevel, user.getLevel());
                throw new BaseException(Code.FORBIDDEN, "权限过高");
            }
        }

        // 权限校验通过，输出日志
        log.info("用户{}执行了{}方法,权限鉴定成功", user.getUsername(), method);
    }
}
