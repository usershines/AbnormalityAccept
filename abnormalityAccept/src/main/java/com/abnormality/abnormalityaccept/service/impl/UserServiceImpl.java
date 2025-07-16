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
import com.abnormality.abnormalityaccept.entity.JwtPayload;
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

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
    private String keyBase;


    @Autowired
    private RedisService redisService;

    @Autowired
    private final UserMapper userMapper;


    /**
     * 查询所有用户
     */
    @Override
    public PageInfo<User> findAllUser(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findAllUser();
        return PageInfo.of(userList);
    }

    /**
     * 根据id查询用户
     */
    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }


    /**
     * 删除数据
     */
    @Override
    public boolean deleteUserById(Long id) {
        int i = userMapper.deleteUserById(id);
        return i > 0;
    }

    /**
     * 新增数据
     */
    //根据实际要求修改过的新增方法
    @Override
    public boolean addUser(User newUser, Long inviterId) {
        if(inviterId <=2) throw new ServiceException(Code.ERROR,"用户级别不足以邀请新用户");
        if(userMapper.selectOne(new QueryWrapper<User>().eq("username",newUser.getUsername())) != null)
            throw new ServiceException(Code.ERROR,"用户名已存在");
        if(inviterId == 3){
            if( newUser.getLevel() >=3) throw new ServiceException(Code.ERROR,"只能设置比自己等级低的级别");
            if(newUser.getFacilityId() != null) throw new ServiceException(Code.ERROR,"只有A级以上用户可以分配工作设施");
        }
        if(inviterId == 4){
            if( newUser.getLevel() >=4) throw new ServiceException(Code.ERROR,"只能设置比自己等级低的级别");
        }
        newUser.setPassword(encryptPassword(newUser.getUsername()+"888"));
        newUser.setInviterId(inviterId);
        newUser.setLeaderId(inviterId);
        return userMapper.addUser(newUser) > 0;
    }



    /**
     * 修改数据
     */
    @Override
    public boolean updateUser(User user) {
        int i = userMapper.updateUser(user);
        return i > 0;
    }

    @Override
    public PageInfo<User> findUserByConditions(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findUserByConditions(user);
        return PageInfo.of(userList);
    }


    // 添加专门的密码更新方法
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        }

        user.setPassword(encryptPassword(newPassword));
        int result = userMapper.updateUser(user);
        return result > 0;
    }

//
//
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
    public AuthResponse register(String username, String password) {
        AuthResponse authResponse = new AuthResponse();
        User user=userMapper.selectOne(new QueryWrapper<User>().eq("username",username) );
        if(ObjectUtil.isNotEmpty(user)){
            throw new BaseException("用户已存在");
        }
        user=new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        String token=generateJwt(user);
        redisService.setEx(getTokenKey(token),token,12*60*60);
        authResponse.setName(user.getUsername());
        authResponse.setToken(token);
        userMapper.insert(user);
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

        JwtPayload jwtPayload=new JwtPayload();
        jwtPayload.setUsername(user.getUsername());
        jwtPayload.setLevel(user.getLevel());


        String token = JWT.create()
                .addPayloads(jwtPayload.toMap())
                .setKey(keytBytes)
                .setExpiresAt(DateUtil.date().offset(DateField.DAY_OF_YEAR,jwtPayload.getExpDays()))
                .sign();
        return token;
    }

    private boolean validateJwt(String token){
        JWT jwt = JWT.of(token);
        JwtPayload payload =jwt.getPayloads().toBean(JwtPayload.class);
        String username= payload.getUsername();
        String key= SecureUtil.md5(keyBase+username);
        byte[] keytBytes= key.getBytes();
        log.info("验证JWT参数");
        log.info("key:{},username:{}",key,username);
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

    //密码加密
    private String encryptPassword(String password){
        String salt= RandomUtil.randomString(16);
        String md5pwd= SecureUtil.md5(salt+password);
        return salt+"$"+md5pwd;
    }
    //验证密码
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

    private JwtPayload getJwtPayload(String token){
        JWT jwt = JWT.of(token);
        return jwt.getPayloads().toBean(JwtPayload.class);
    }



}




