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
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.RedisService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    @Value("${jwt.keybase}")
    private String jwtSecret;


//    @Autowired
//    private RedisService redisService;

    @Autowired
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
    public User findUserById(Long id){
        return userMapper.findUserById(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    /**
     * 删除数据
     */
    @Override
    public boolean deleteUserById(Long id){
        int i = userMapper.deleteUserById(id);
        return i > 0;
    }

    /**
     * 新增数据
     */
    @Override
    public boolean addUser(User newUser, Long inviterId) {
        // 1. 验证邀请人是否存在
        User inviter = userMapper.findUserById(inviterId);
        if (ObjectUtil.isNull(inviter)) {
            throw new ServiceException(Code.NOT_FOUND, "邀请人不存在");
        }

        // 2. 验证用户名是否已存在
        User existingUser = userMapper.findUserById(newUser.getId());
        if (ObjectUtil.isNotNull(existingUser)) {
            throw new ServiceException(Code.NOT_ACCEPTABLE, "用户名已存在");
        }

        // 3. 权限检查 - 只有B级以上用户可邀请新用户
        if (inviter.getLevel() < 2) { // 假设0-4级，2为B级
            throw new ServiceException(Code.FORBIDDEN, "权限不足，需要B级及以上权限");
        }

        // 4. 检查用户等级是否合法
        if (newUser.getLevel() > inviter.getLevel()) {
            throw new ServiceException(Code.FORBIDDEN, "不能创建高于自己等级的用户");
        }

        // 5. 设置新用户默认值
        newUser.setPassword(encryptPassword(newUser.getUsername() + "123"));
        newUser.setInviterId(inviterId);
        newUser.setLeaderId(inviterId);
        newUser.setEmail("");
        newUser.setIntroduction("神秘特工");

        // 6. 插入新用户
        int result = userMapper.addUser(newUser);
        return result > 0;
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
    public PageInfo<User> findUserByConditions(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findUserByConditions(user);
        return PageInfo.of(userList);
    }



    @Override
    public AuthResponse login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findUserByUsername(username);
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        }

        // 2. 验证密码
        if (!verifyPassword(password, user.getPassword())) {
            throw new ServiceException(Code.UNAUTHORIZED, "密码错误");
        }

        // 3. 生成JWT令牌
        String token = generateJwt(user);

        // 4. 创建响应
        AuthResponse response = new AuthResponse();
        response.setName(user.getUsername());
        response.setToken(token);
        return response;
    }

    @Override
    public boolean verify(String token) {
        try {
            // 1. 解析JWT
            JWT jwt = JWT.of(token);

            // 2. 验证签名
            if (!JWTUtil.verify(token, jwtSecret.getBytes())) {
                return false;
            }

            // 3. 验证有效期
            JWTValidator.of(token).validateDate(DateUtil.date());

            // 4. 验证用户是否存在
            String username = jwt.getPayload("username").toString();
            User user = userMapper.findUserByUsername(username);
            return ObjectUtil.isNotNull(user);

        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }


    private String generateJwt(User user) {
        return JWT.create()
                .setPayload("userId", user.getId())
                .setPayload("username", user.getUsername())
                .setPayload("level", user.getLevel())
                .setKey(jwtSecret.getBytes())
                .setExpiresAt(DateUtil.date().offset(DateField.HOUR, 12))
                .sign();
    }

    private String encryptPassword(String password) {
        return SecureUtil.md5(password); // 简单MD5加密
    }

    private boolean verifyPassword(String password, String storedPassword) {
        return SecureUtil.md5(password).equals(storedPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 用户角色根据等级确定
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getLevel() >= 4) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (user.getLevel() >= 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
//
//    @Override
//    public AuthResponse login(String username, String password) {
//        AuthResponse authResponse = new AuthResponse();
//        User user=userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
//        if(ObjectUtil.isNull(user)){
//            throw new BaseException("用户不存在");
//        }
//        if(!verifyPassword(password,user.getPassword())){
//            throw new BaseException("密码错误");
//        }
//        authResponse.setName(user.getUsername());
//        String token=generateJwt(user);
//        redisService.setEx(getTokenKey(token),token,12*60*60);
//        authResponse.setToken(token);
//        return authResponse;
//
//    }
//
//
//    @Override
//    public boolean logout(String token) {
//        if(redisService.exists(getTokenKey(token))){
//            redisService.delete(getTokenKey(token));
//        }
//        return true;
//    }
//
//    @Override
//    public boolean verify(String token) {
//        if(!redisService.exists(getTokenKey(token))){
//            throw new BaseException(Code.ERROR,"用户未登录");
//        }
//        try{
//            return validateJwt(token);
//        } catch (Exception e) {
//            if(redisService.exists(getTokenKey(token))){
//                redisService.delete(getTokenKey(token));
//            }
//            throw new BaseException(Code.ERROR,"用户未登录",e);
//        }
//
//    }
//
//    private String generateJwt(User user){
//        String key= SecureUtil.md5(keyBase+user.getUsername());
//        byte[] keytBytes= key.getBytes();
//        log.info("生成JWT参数");
//        log.info("key:{},username:{},",key,user.getUsername());
//        String token = JWT.create()
//                .setPayload("username",user.getUsername())
//                .setKey(keytBytes)
//                .setExpiresAt(DateUtil.date().offset(DateField.DAY_OF_YEAR,7))
//                .sign();
//        return token;
//    }
//
//    private boolean validateJwt(String token){
//        JWT jwt = JWT.of(token);
//        String username= jwt.getPayload("username").toString();
//        String role= jwt.getPayload("role").toString();
//        String key= SecureUtil.md5(keyBase+username+role);
//        byte[] keytBytes= key.getBytes();
//        log.info("验证JWT参数");
//        log.info("key:{},username:{},role:{}",key,username,role);
//        JWTValidator validator = JWTValidator.of(token);
//        try{
//            validator.validateDate(DateUtil.date());
//        } catch (ValidateException e) {
//            throw new BaseException(Code.ERROR,"用户未登录",e);
//        }
//
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("username",username);
//        User user= userMapper.selectOne(queryWrapper);
//        if(user==null){
//            throw new BaseException(Code.ERROR,"用户不存在");
//        }
//        boolean result=JWTUtil.verify(token,keytBytes);
//        log.info("用户名：{} 验证结果：{}",username,result);
//
//        return result;
//    }
//    private String encryptPassword(String password){
//        String salt= RandomUtil.randomString(16);
//        String md5pwd= SecureUtil.md5(salt+password);
//        return salt+"$"+md5pwd;
//    }
//
//    private boolean verifyPassword(String password,String inputPwd){
//        String salt=inputPwd.substring(0,inputPwd.indexOf("$"));
//        String md5pwd= SecureUtil.md5(salt+password);
//        return inputPwd.substring(inputPwd.indexOf("$")+1).equals(md5pwd);
//    }
//
//    private String getTokenKey(String token){
//        JWT jwt = JWT.of(token);
//        String username= jwt.getPayload("username").toString();
//        return username;
//    }

}
