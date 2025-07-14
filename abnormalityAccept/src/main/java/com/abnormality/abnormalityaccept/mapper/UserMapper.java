package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    User findUserById(Long id);

    /**
     * 新增数据
     */
    int adduser(User user);

    /**
     * 删除数据
     */
    int deleteUserById(Long id);

    /**
     * 修改数据
     */
    int updateUser(User user);

    // 多条件查询用户
    List<User> findUserByConditions(
            @Param("id") Long id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("level") Integer level,
            @Param("facilityId") Long facilityId
    );
}
