//package com.abnormality.abnormalityaccept.service.impl;
//
//import com.abnormality.abnormalityaccept.entity.Team;
//import com.abnormality.abnormalityaccept.entity.User;
//import org.springframework.stereotype.Service;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-13
// */
//@Service
//public class TeamServiceImpl {
//
//}
package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.service.TeamService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final UserMapper userMapper;
    private final QuestMapper questMapper;

    /**
     * 创建新的机动小队（需O5权限）
     */
    @Override
    @Transactional
    public Team createTeam(Team team, User creator) {
        // 1. 校验创建者权限（O5权限）
        if (!"O5".equals(creator.getRole())) {
            throw new BaseException("权限不足：仅O5议会成员可创建机动小队");
        }
        // 2. 校验小队名称唯一性
        if (teamMapper.countByName(team.getName()) > 0) {
            throw new BaseException("小队名称已存在：" + team.getName());
        }
        // 3. 填充默认值
        team.setStatus(Integer.parseInt("待命")); // 新创建小队默认状态为“待命”
        // 4. 插入数据库
        teamMapper.addTeam(team);
        // 5. 返回创建的小队（包含自增ID）
        return teamMapper.findTeamById(team.getId());
    }

    /**
     * 更新小队信息
     */
    @Override
    @Transactional
    public Team updateTeam(Long id, Team updatedData) {
        // 1. 校验小队是否存在
        Team existingTeam = teamMapper.findTeamById(id);
        if (existingTeam == null) {
            throw new BaseException("小队不存在：ID=" + id);
        }
        // 2. 仅更新允许修改的字段（避免覆盖状态、任务等关键信息）
        existingTeam.setName(updatedData.getName());
        existingTeam.setDescription(updatedData.getDescription());
        existingTeam.setLevel(updatedData.getLevel());
        existingTeam.setLeaderId(updatedData.getLeaderId());
        // 3. 执行更新
        teamMapper.updateTeam(existingTeam);
        return teamMapper.findTeamById(id);
    }

    /**
     * 指派任务给小队
     */
    @Override
    @Transactional
    public void assignQuestToTeam(Long teamId, Long questId) {
        // 1. 校验小队和任务是否存在
        if (teamMapper.findTeamById(teamId) == null) {
            throw new BaseException("小队不存在：ID=" + teamId);
        }
        if (questMapper.findQuestById(questId) == null) {
            throw new BaseException("任务不存在：ID=" + questId);
        }
        // 2. 校验小队当前状态（仅“待命”状态可接收任务）
        Team team = teamMapper.findTeamById(teamId);
        if (!"待命".equals(team.getStatus())) {
            throw new BaseException("小队状态异常：当前状态为" + team.getStatus() + "，无法接收任务");
        }
        // 3. 指派任务并更新状态为“执行任务”
        teamMapper.updateQuestId(teamId, questId);
        teamMapper.updateStatus(teamId, "执行任务");
    }

    /**
     * 更新小队状态
     */
    @Override
    @Transactional
    public void updateTeamStatus(Long teamId, String newStatus) {
        // 1. 校验状态合法性
        if (!"待命".equals(newStatus) && !"执行任务".equals(newStatus) && !"休整".equals(newStatus)) {
            throw new BaseException("无效状态：" + newStatus + "，允许值：待命/执行任务/休整");
        }
        // 2. 校验小队是否存在
        if (teamMapper.findTeamById(teamId) == null) {
            throw new BaseException("小队不存在：ID=" + teamId);
        }
        // 3. 若状态改为“待命”，需清空任务ID
        if ("待命".equals(newStatus)) {
            teamMapper.updateQuestId(teamId, null);
        }
        // 4. 更新状态
        teamMapper.updateStatus(teamId, newStatus);
    }

    /**
     * 添加成员到小队
     */
    @Override
    @Transactional
    public void addMemberToTeam(Long teamId, Long userId) {
        // 1. 校验小队和用户是否存在
        if (teamMapper.findTeamById(teamId) == null) {
            throw new BaseException("小队不存在：ID=" + teamId);
        }
        if (userMapper.findUserById(userId) == null) {
            throw new BaseException("用户不存在：ID=" + userId);
        }
        // 2. 校验用户是否已在小队中
        if (teamMapper.checkMemberExists(teamId, userId)) {
            throw new BaseException("用户已在小队中：用户ID=" + userId + "，小队ID=" + teamId);
        }
        // 3. 添加成员（假设存在team_member关联表）
        teamMapper.insertMember(teamId, userId);
    }

    /**
     * 移除小队成员
     */
    @Override
    @Transactional
    public void removeMemberFromTeam(Long teamId, Long userId) {
        // 1. 校验小队和用户是否存在
        if (teamMapper.findTeamById(teamId) == null) {
            throw new BaseException("小队不存在：ID=" + teamId);
        }
        if (userMapper.findUserById(userId) == null) {
            throw new BaseException("用户不存在：ID=" + userId);
        }
        // 2. 校验用户是否在小队中
        if (!teamMapper.checkMemberExists(teamId, userId)) {
            throw new BaseException("用户不在小队中：用户ID=" + userId + "，小队ID=" + teamId);
        }
        // 3. 移除成员
        teamMapper.deleteMember(teamId, userId);
    }

    /**
     * 获取小队详情（包含成员信息）
     */
    @Override
    public Team getTeamDetails(Long teamId) {
        Team team = teamMapper.findTeamById(teamId);
        if (team == null) {
            throw new BaseException("小队不存在：ID=" + teamId);
        }
        // 关联查询成员列表
        team.setMembers(teamMapper.findMembersByTeamId(teamId));
        return team;
    }
}