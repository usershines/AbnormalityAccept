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
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
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
@Slf4j
public class UserServiceImpl implements UserService {


    @Value("${jwt.keybase}")
    private String keyBase;

    @Autowired
    private RedisService redisService;

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
    public AuthResponse login(String username, String password) {
        AuthResponse authResponse = new AuthResponse();
        User user=userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
        if(ObjectUtil.isNull(user)){
            throw new BaseException("用户不存在");
        }
        if(!verifyPassword(password,user.getPassword())){
            throw new BaseException("密码错误");
        }
        authResponse.setName(user.getUsername());
        String token=generateJwt(user);
        redisService.setEx(getTokenKey(token),token,12*60*60);
        authResponse.setToken(token);
        return authResponse;

    }

    @Override
    public AuthResponse register(String username, String password, String email) {
        AuthResponse authResponse = new AuthResponse();
        User user=userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
        if(ObjectUtil.isNotNull(user)){
            throw new BaseException("用户已存在");
        }
        user=new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        user.setEmail(email);
        addUser(user);
        authResponse.setName(user.getUsername());
        String token=generateJwt(user);
        redisService.setEx(getTokenKey(token),token,12*60*60);
        authResponse.setToken(token);
        return authResponse;
    }

    @Override
    public boolean logout(String token) {
        if(redisService.exists(getTokenKey(token))){
            redisService.delete(getTokenKey(token));
        }
        return true;
    }

    @Override
    public boolean verify(String token) {
        if(!redisService.exists(getTokenKey(token))){
            throw new BaseException(Code.ERROR,"用户未登录");
        }
        try{
            return validateJwt(token);
        } catch (Exception e) {
            if(redisService.exists(getTokenKey(token))){
                redisService.delete(getTokenKey(token));
            }
            throw new BaseException(Code.ERROR,"用户未登录",e);
        }

    }

    private String generateJwt(User user){
        String key= SecureUtil.md5(keyBase+user.getUsername());
        byte[] keytBytes= key.getBytes();
        log.info("生成JWT参数");
        log.info("key:{},username:{},",key,user.getUsername());
        String token = JWT.create()
                .setPayload("username",user.getUsername())
                .setKey(keytBytes)
                .setExpiresAt(DateUtil.date().offset(DateField.DAY_OF_YEAR,7))
                .sign();
        return token;
    }

    private boolean validateJwt(String token){
        JWT jwt = JWT.of(token);
        String username= jwt.getPayload("username").toString();
        String role= jwt.getPayload("role").toString();
        String key= SecureUtil.md5(keyBase+username+role);
        byte[] keytBytes= key.getBytes();
        log.info("验证JWT参数");
        log.info("key:{},username:{},role:{}",key,username,role);
        JWTValidator validator = JWTValidator.of(token);
        try{
            validator.validateDate(DateUtil.date());
        } catch (ValidateException e) {
            throw new BaseException(Code.ERROR,"用户未登录",e);
        }

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user= userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BaseException(Code.ERROR,"用户不存在");
        }
        boolean result=JWTUtil.verify(token,keytBytes);
        log.info("用户名：{} 验证结果：{}",username,result);

        return result;
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

    private String getTokenKey(String token){
        JWT jwt = JWT.of(token);
        String username= jwt.getPayload("username").toString();
        return username;
    }

    private String getNewUserNumber(){
        return String.valueOf(userMapper.selectCount(null)+1);
    }
}
