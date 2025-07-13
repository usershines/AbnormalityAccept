package com.abnormality.abnormalityaccept.service.impl;

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
}
