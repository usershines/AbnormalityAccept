//package com.abnormality.abnormalityaccept.Filter;
//
//import com.abnormality.abnormalityaccept.exception.CaptchaException;
//import com.abnormality.abnormalityaccept.handler.LoginFailureHandler;
//import com.alibaba.excel.util.StringUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-15
// */
//@Component
//public class CaptchaFilter extends OncePerRequestFilter {
//    @Autowired
//    RedisUtil redisUtil;
//    @Autowired
//    LoginFailureHandler loginFailureHandler;
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        String url = httpServletRequest.getRequestURI();
//        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {
//            // 校验验证码
//            try {
//                validate(httpServletRequest);
//            } catch (CaptchaException e) {
//                // 交给认证失败处理器
//                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
//            }
//        }
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
//    }
//    // 校验验证码逻辑
//    private void validate(HttpServletRequest httpServletRequest) {
//        String code = httpServletRequest.getParameter("code");
//        String key = httpServletRequest.getParameter("userKey");
//        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
//            throw new CaptchaException("验证码错误");
//        }
//        if (!code.equals(redisUtil.hget(Const.CAPTCHA_KEY, key))) {
//            throw new CaptchaException("验证码错误");
//        }
//        // 若验证码正确，执行以下语句
//        // 一次性使用
//        redisUtil.hdel(Const.CAPTCHA_KEY, key);
//    }
//}