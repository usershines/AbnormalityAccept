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

import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.dto.request.TeamUpdateRequest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.service.TeamService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final UserMapper userMapper;
    private final QuestMapper questMapper;

    @Override
    @Level(allowLevel = {5})
    public PageInfo<Team> findAllTeam(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Team> teamList = teamMapper.findAllTeam();
        return PageInfo.of(teamList);
    }

    @Override
    @Level(allowLevel = {5})
    public Team findTeamById(Long id) {
        return teamMapper.findTeamById(id);
    }


    @Override
    @Level(allowLevel = {5})
    public PageInfo<User> findUserBelongNotTeam(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findUserBelongNotTeam();
        return PageInfo.of(userList);
    }

    @Override
    @Level(allowLevel = {5})
    public boolean createTeam(Team team, Long leaderId) {
        team.setLeaderId(leaderId);
        return teamMapper.addTeam(team)>0;
    }

    @Override
    @Level(allowLevel = {5})
    public boolean updateTeam(TeamUpdateRequest updatedData) {
        if(updatedData.getStatus()<0 || updatedData.getStatus()>3) throw new ServiceException(Code.BAD_REQUEST, "非法参数");
        if(updatedData.getLevel()<0 || updatedData.getLevel()>5) throw new ServiceException(Code.BAD_REQUEST, "非法参数");
        if(updatedData.getLeaderId() == null) throw new ServiceException(Code.BAD_REQUEST, "队长不能为空");
        Team team = new Team();
        team.setName(updatedData.getName());
        team.setStatus(updatedData.getStatus());
        team.setLevel(updatedData.getLevel());
        team.setResolvingQuestId(updatedData.getResolvingQuestId());
        team.setLeaderId(updatedData.getLeaderId());
        team.setDescription(updatedData.getDescription());
        return teamMapper.updateTeam(team)>0;
    }

    @Override
    @Level(allowLevel = 5)
    public boolean addMemberToTeam(Long teamId, Long userId) {
        Team team = teamMapper.findTeamById(teamId);
        User  user = userMapper.findUserById(userId);
        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
        if (user.getTeamId()!= null) throw new ServiceException(Code.BAD_REQUEST, "用户已加入小队");
        team.getMemberIds().add(userId);
        user.setTeamId(teamId);
        userMapper.updateUser(user);
        return teamMapper.updateTeam(team)>0;
    }

    @Override
    @Level(allowLevel = {5})
    public boolean removeMemberFromTeam(Long teamId, Long userId) {
        Team team = teamMapper.findTeamById(teamId);
        User  user = userMapper.findUserById(userId);
        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
        if (user.getTeamId()== null) throw new ServiceException(Code.BAD_REQUEST, "用户未加入小队");
        if (!team.getMemberIds().contains(userId)) throw new ServiceException(Code.BAD_REQUEST, "用户未加入该小队");
        team.getMemberIds().remove(userId);
        user.setTeamId(null);
        userMapper.updateUser(user);
        return teamMapper.updateTeam(team) > 0;
    }


//    @Override
//    @Level(allowLevel = 5)
//    public List<User> getTeamDetails(Long teamId) {
//        Team team = teamMapper.findTeamById(teamId);
//        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
//        List< User>  users = userMapper.findUserByTeamId(teamId);
//
//
//    }

    @Override
    @Level(allowLevel = 5)
    public boolean deleteTeamById(Long teamId) {
        Team team = teamMapper.findTeamById(teamId);
        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
        List< User>  users = userMapper.findUserByTeamId(teamId);
        for (User user : users) {
            user.setTeamId(null);
            userMapper.updateUser(user);
        }
        return teamMapper.deleteTeamById(teamId)>0;
    }

}