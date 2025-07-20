package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.EditSubordinateRequest;
import com.abnormality.abnormalityaccept.dto.request.InviteRequest;
import com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest;
import com.abnormality.abnormalityaccept.dto.request.UserParamRequest;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.User;
import com.github.pagehelper.PageInfo;
//import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.List;

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
    PageInfo<User> findAllUser(Integer pageNum, Integer pageSize, Long finderId);
    /**
     *根据id查询用户
     */
    User findUserById(Long id,Long finderId);
    User findUserByName(String name);


    /**
     * 删除数据
     */
    boolean deleteUserById(Long id,Long editor);

    /**
     * 新增数据
     */
    // 修改后的addUser方法
    boolean addUser(InviteRequest inviteRequest, Long inviterId);

    /**
     * 修改数据
     */
    boolean editSubordinate(EditSubordinateRequest editSubordinateRequest, Long editorId);

    boolean updateUser(UpdateUserOneSelfRequest updateUserOneSelfRequest, Long editorId);


    /** 多条件查询用户（分页） */
    PageInfo<User> findUserByConditions(UserParamRequest userParamRequest);



//
//    /** 验证密码 */
//
     boolean updatePassword(Long id, String newPassword);

    AuthResponse login(String username, String password);

    AuthResponse register(String username, String password);

    boolean regist(String username, String password ,String email);


    boolean logout(String token);

    /**
     * 验证token,只验证用户名与过期时间
     * @param token
     * @return true-验证成功，false-验证失败
     */
    boolean verify(String token);

    Long getUserIdByToken();

    PageInfo<User> findByFacilityId(Long facilityId, Integer pageNum, Integer pageSize);

//
//

}
