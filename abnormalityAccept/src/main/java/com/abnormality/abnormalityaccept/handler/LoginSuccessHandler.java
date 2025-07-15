//package com.abnormality.abnormalityaccept.handler;
//
//import cn.hutool.json.JSONUtil;
//import com.abnormality.abnormalityaccept.Utils.JwtUtils;
//import com.abnormality.abnormalityaccept.dto.Result;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletOutputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
//public class LoginSuccessHandler implements AuthenticationSuccessHandler {
//    @Autowired
//    JwtUtils jwtUtils;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException, IOException {
//        httpServletResponse.setContentType("application/json;charset=UTF-8");
//        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
//        // 生成JWT，并放置到请求头中
//        String jwt = jwtUtils.generateToken(authentication.getName());
//        httpServletResponse.setHeader(jwtUtils.getHeader(), jwt);
//        Result<String> result = Result.ok("SuccessLogin");
//        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
//        outputStream.flush();
//        outputStream.close();
//    }
//}