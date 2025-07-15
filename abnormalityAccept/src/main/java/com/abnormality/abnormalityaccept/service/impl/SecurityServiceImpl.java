//package com.abnormality.abnormalityaccept.service.impl;
//
//import com.abnormality.abnormalityaccept.entity.User;
//import com.abnormality.abnormalityaccept.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.security.core.Authentication;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-14
// */
//@Component("securityService")
//public class SecurityServiceImpl {
//
//    @Autowired
//    private UserService userService;
//
//    public boolean isBLevelOrHigher() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return false;
//        }
//
//        String username = authentication.getName();
//        User user = userService.findUserByUsername(username);
//        return user != null && user.getLevel() >= 2; // B级或以上
//    }
//}
