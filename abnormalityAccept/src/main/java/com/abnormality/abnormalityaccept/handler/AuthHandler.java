package com.abnormality.abnormalityaccept.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 认证处理器，用于处理与 Token 验证相关的认证逻辑。
 * 该类通过注入的用户服务（UserService）来完成实际的 Token 验证操作。
 * <p><strong>该类只负责验证是否登录，不负责权限限制验证</strong></p>
 */
@Component
public class AuthHandler {

    /**
     * 用户服务实例，用于执行 Token 的最终验证逻辑。
     * 通过 Spring 自动注入的方式获取 UserService 实例。
     */
    @Autowired
    private UserService userService;

    /**
     * 验证提供的 Token 是否有效。
     *
     * <p>该方法首先检查传入的 Token 是否为空或空白字符串。如果 Token 无效，
     * 则抛出 ServiceException 异常，提示用户未登录。否则，将验证逻辑委托给
     * UserService 的 verify 方法进行进一步的验证。</p>
     *
     * @param token 需要验证的 Token 字符串
     * @return 如果 Token 有效，返回 true；否则返回 false
     * @throws ServiceException 如果 Token 为空或空白字符串，抛出未认证的异常
     */
    public boolean verify(String token) {
        // 检查 Token 是否为空或空白字符串
        if (StrUtil.isBlankIfStr(token)) {
            throw new ServiceException(Code.NOT_AUTHENTICATED, "用户未登录");
        }

        // 最终 Token 校验，交由 UserService 处理实际验证逻辑
        return userService.verify(token);
    }
}
