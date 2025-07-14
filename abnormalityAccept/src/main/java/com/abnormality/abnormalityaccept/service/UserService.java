package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.User;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
public interface UserService {
    /**
     *查询所有用户
     */
    PageInfo<User> findAllUser(Integer pageNum, Integer pageSize);
    /**
     *根据id查询用户
     */
    User findUserById(Long id);

    /**
     * 删除数据
     */
    boolean deleteUserById(Long id);

    /**
     * 新增数据
     */
    // 修改后的addUser方法
    boolean addUser(User newUser, Long inviterId);

    /**
     * 修改数据
     */
    boolean updateUser(User user);

    AuthResponse login(String username, String password);


    boolean logout(String token);

    boolean verify(String token);



}
