package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.github.pagehelper.PageInfo;

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

//    // 创建新的机动小队（O5权限）
//    Team createTeam(Team team);
//
//    // 更新小队信息
//    Team updateTeam(Long teamId, Team updatedData);
//
//    // 指派任务给小队
//    void assignQuestToTeam(Long teamId, Long questId);
//
//    // 更新小队状态
//    void updateTeamStatus(Long teamId, String newStatus);
//
//    // 添加成员到小队
//    void addMemberToTeam(Long teamId, Long userId);
//
//    // 移除小队成员
//    void removeMemberFromTeam(Long teamId, Long userId);
//
//    // 获取小队详情
//    Team getTeamDetails(Long teamId);
}
