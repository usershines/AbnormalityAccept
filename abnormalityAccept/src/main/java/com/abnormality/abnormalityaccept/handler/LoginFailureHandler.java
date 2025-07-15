//package com.abnormality.abnormalityaccept.handler;
//
//import cn.hutool.json.JSONUtil;
//import com.abnormality.abnormalityaccept.dto.Result;
//import com.abnormality.abnormalityaccept.exception.CaptchaException;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletOutputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-15
// */
//@Component
//public class LoginFailureHandler implements AuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//        httpServletResponse.setContentType("application/json;charset=UTF-8");
//        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
//        String errorMessage = "用户名或密码错误";
//        Result result;
//        if (e instanceof CaptchaException) {
//            errorMessage = "验证码错误";
//            result = Result.error(errorMessage);
//        } else {
//            result = Result.error(errorMessage);
//        }
//        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
//        outputStream.flush();
//        outputStream.close();
//    }
//}