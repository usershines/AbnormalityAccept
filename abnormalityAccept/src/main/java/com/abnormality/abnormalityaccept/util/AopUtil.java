package com.abnormality.abnormalityaccept.util;


import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

public class AopUtil {


    public static String getToken(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes)
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String oToken=request.getHeader("Authorization");

        if(StrUtil.isNotBlank(oToken)){
            if(oToken.startsWith("Bearer ")){
                return oToken.substring("Bearer ".length());
            }else{
                return oToken;
            }
        }
        return "";
    }
}
