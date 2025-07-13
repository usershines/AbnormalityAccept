package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     *查询所有用户
     */
    List<User> findAllUser();

    /**
     *根据id查询用户
     */
    User findUserById(Integer id);

    /**
     * 新增数据
     */
    int adduser(User user);

    /**
     * 删除数据
     */
    int deleteUserById(Integer id);

    /**
     * 修改数据
     */
    int updateUser(User user);
}
