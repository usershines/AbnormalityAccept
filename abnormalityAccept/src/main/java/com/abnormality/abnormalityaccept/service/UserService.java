package com.abnormality.abnormalityaccept.service;

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
    User findUserById(Integer id);

    /**
     * 删除数据
     */
    boolean deleteUserById(Integer id);

    /**
     * 新增数据
     */
    boolean addUser(User user);

    /**
     * 修改数据
     */
    boolean updateUser(User user);

    String login(String username, String password);

    String register(String username, String password, String email);

    boolean logout(String token);

    boolean verify(String token);

}
