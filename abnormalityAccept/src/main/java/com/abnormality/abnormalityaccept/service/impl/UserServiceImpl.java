package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /**
     *查询所有用户
     */
    @Override
    public PageInfo<User> findAllUser(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.findAllUser();
        return PageInfo.of(userList);
    }

    /**
     *根据id查询用户
     */
    @Override
    public User findUserById(Integer id){
        return userMapper.findUserById(id);
    }

    /**
     * 删除数据
     */
    @Override
    public boolean deleteUserById(Integer id){
        int i = userMapper.deleteUserById(id);
        return i > 0;
    }

    /**
     * 新增数据
     */
    @Override
    public boolean addUser(User user){
        int i = userMapper.adduser(user);
        return i > 0;
    }

    /**
     * 修改数据
     */
    @Override
    public boolean updateUser(User user){
        int i = userMapper.updateUser(user);
        return i > 0;
    }

    @Override
    public String login(String username, String password) {
        return "";
    }

    @Override
    public String register(String username, String password, String email) {
        return "";
    }

    @Override
    public boolean logout(String token) {
        return false;
    }

    @Override
    public boolean verify(String token) {
        return false;
    }


    private String encryptPassword(String password){
        String salt= RandomUtil.randomString(16);
        String md5pwd= SecureUtil.md5(salt+password);
        return salt+"$"+md5pwd;
    }

    private boolean verifyPassword(String password,String inputPwd){
        String salt=inputPwd.substring(0,inputPwd.indexOf("$"));
        String md5pwd= SecureUtil.md5(salt+password);
        return inputPwd.substring(inputPwd.indexOf("$")+1).equals(md5pwd);
    }
}
