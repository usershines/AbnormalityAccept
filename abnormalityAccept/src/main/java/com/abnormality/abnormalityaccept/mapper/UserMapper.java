package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.dto.request.UserParamRequest;
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
     *根据小队Id查询用户
     */
    List<User> findUserByTeamId(Long teamId);

    /**
     *查询暂无归属小队的用户列表
     */
    List< User> findUserBelongNotTeam();

    List<User> findUserByLeaderId(Long leaderId);
    List<User> findUserByInviterId(Long inviteId);

    /**
     * 根据id查询无归属小队的用户
     */
     User findUserBelongNotTeamById(Long id);

    /**
     * 新增数据
     */
    int addUser(@Param("user") User user);

    /**
     * 修改下属数据
     */
    int editSubordinate(@Param("user") User user );

    /**
     * 删除数据,逻辑删除
     */
    int deleteUserById(Long id);

    /**
     * 修改自己信息
     */
    int updateUser( @Param("user") User user);

    int updateUserAll( @Param("user") User user);

    int updatePassword(Long userId);
    // 多条件查询用户
    List<User> findUserByConditions(UserParamRequest userParamRequest);
    
    User findUserByName(String name);


}
