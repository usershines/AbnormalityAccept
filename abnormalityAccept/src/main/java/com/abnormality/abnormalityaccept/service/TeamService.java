package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.request.TeamUpdateRequest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.TeamParam;
import com.abnormality.abnormalityaccept.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface TeamService {

    //查询所有小队
    PageInfo<Team> findAllTeam(Integer pageNum, Integer pageSize);

    //根据id查询小队
    Team findTeamById(Long teamId);

    //查询暂无机动小队归属的用户列表
    PageInfo<User> findUserBelongNotTeam(Integer pageNum, Integer pageSize);

    //创建新的机动小队（O5权限）
    boolean createTeam(Team team ,Long leaderId);

    // 更新小队信息
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest);

    // 添加成员到小队
    boolean addMemberToTeam(Long teamId, Long userId);

    // 移除小队成员
    boolean removeMemberFromTeam(Long teamId, Long userId);

    // 删除小队
    boolean deleteTeamById(Long teamId);

    // 分页多条件查询
    PageInfo<Team> findTeamByConditions(TeamParam teamParam);

    //查询小队成员信息
    List< User> findTeamMember(Long teamId);

//    // 获取小队详情
//    List<User> getTeamDetails(Long teamId);
}
