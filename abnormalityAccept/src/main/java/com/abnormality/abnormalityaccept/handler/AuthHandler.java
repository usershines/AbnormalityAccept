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
 * 认证处理类，用于校验用户请求中的 JWT Token 合法性及权限有效性。
 *
 * <p>该类为 Spring 组件，通过注入的 UserService 执行最终的 Token 验证逻辑。</p>
 */
@Component
public class AuthHandler {

    /**
     * 用户服务实例，用于执行 Token 的最终验证逻辑。
     */
    @Autowired
    private UserService userService;

    /**
     * 校验传入的 Token 是否有效，并验证用户权限等级。
     *
     * <p>该方法主要完成以下操作：</p>
     * <ul>
     *   <li>检查 Token 是否为空或空白字符串，若为空则抛出未登录异常。</li>
     *   <li>解析 JWT Token 并将其负载部分转换为 JwtPayload 对象。</li>
     *   <li>校验负载中的权限等级字段（level），如果权限不足（level == 0）则抛出权限不足异常。</li>
     *   <li>调用 UserService 进行最终的 Token 正确性校验。</li>
     * </ul>
     *
     * @param token 请求中携带的 JWT Token
     * @return 如果 Token 有效且权限满足要求，则返回 true
     * @throws ServiceException 如果 Token 无效、为空或权限不足时抛出相应业务异常
     */
    public boolean verify(String token) {
        // 检查 Token 是否为空或空白字符串
        if (StrUtil.isBlankIfStr(token)) {
            throw new ServiceException(Code.NOT_AUTHENTICATED, "用户未登录");
        }

        // 解析 JWT Token
        JWT jwt = JWT.of(token);
        // 获取并转换 Token 中的负载信息为 JwtPayload 类型
        JwtPayload payload = jwt.getPayloads().toBean(JwtPayload.class);

        // 获取用户权限等级
        int level = payload.getLevel();

        // 检查权限等级是否为最低（0），若是则抛出权限不足异常
        if (level == 0) {
            throw new ServiceException(Code.FORBIDDEN, "权限不足");
        }

        // 最终 Token 校验，交由 UserService 处理实际验证逻辑
        return userService.verify(token);
    }
}
