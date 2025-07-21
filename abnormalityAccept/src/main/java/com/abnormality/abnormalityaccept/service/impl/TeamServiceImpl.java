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

import cn.hutool.core.util.ObjectUtil;
import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.dto.request.TeamRequest;
import com.abnormality.abnormalityaccept.dto.request.TeamUpdateRequest;
import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.UserAndTeam;
import com.abnormality.abnormalityaccept.entity.param.TeamParam;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserAndTeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.service.TeamService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final UserMapper userMapper;
    private final QuestMapper questMapper;

    private final UserAndTeamMapper userAndTeamMapper;

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
    public boolean createTeam(TeamRequest  teamRequest) {
        Team oldTeam = teamMapper.findTeamByName(teamRequest.getName());
        if(oldTeam != null ) throw new ServiceException(Code.ERROR,"小队已存在");
        User leader = userMapper.findUserById(teamRequest.getLeaderId());
        if(leader == null) throw new ServiceException(Code.ERROR,"用户不存在");
        if(leader.getTeamId()!=null) throw new ServiceException(Code.ERROR,"用户已加入小队");

        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setLevel(teamRequest.getLevel());
        team.setDescription(teamRequest.getDescription());
        team.setLeaderId(teamRequest.getLeaderId());
        team.setStatus(0);
        teamMapper.insert( team);

        leader.setTeamId(team.getId());
        userMapper.updateUserAll(leader);

        UserAndTeam userAndTeam = new UserAndTeam();
        userAndTeam.setTeamId(team.getId());
        userAndTeam.setUserId(leader.getId());
        return userAndTeamMapper.insert(userAndTeam)>0;
    }

    @Override
    @Level(allowLevel = {5})
    public boolean updateTeam(TeamUpdateRequest updatedData) {
        if(updatedData.getStatus()<0 || updatedData.getStatus()>3) throw new ServiceException(Code.BAD_REQUEST, "非法参数");
        if(updatedData.getLevel()<0 || updatedData.getLevel()>5) throw new ServiceException(Code.BAD_REQUEST, "非法参数");
        if(updatedData.getLeaderId() == null) throw new ServiceException(Code.BAD_REQUEST, "队长不能为空");
        Team team = teamMapper.selectById(updatedData.getId());
        String name = updatedData.getName();
        List<Quest> quests = questMapper.selectList(new QueryWrapper<Quest>().eq("quest_name", name));
        for(Quest quest:quests){
            quest.setResolvingByTeamName(name);
            questMapper.updateQuest(quest);
        }
        team.setName(name);
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
        UserAndTeam userAndTeam = new UserAndTeam();
        userAndTeam.setTeamId(teamId);
        userAndTeam.setUserId(userId);
        userAndTeamMapper.insert(userAndTeam);
        user.setTeamId(teamId);
        userMapper.updateUserAll(user);
        return teamMapper.updateTeam(team)>0;
    }

    @Override
    @Level(allowLevel = {5})
    public boolean removeMemberFromTeam(Long teamId, Long userId) {
        Team team = teamMapper.findTeamById(teamId);
        User  user = userMapper.findUserById(userId);
        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
        if (user.getTeamId()== null) throw new ServiceException(Code.BAD_REQUEST, "用户未加入小队");
        UserAndTeam userAndTeam = userAndTeamMapper.selectOne(new QueryWrapper<UserAndTeam>().eq("user_id", userId));
        if(userAndTeam == null || ObjectUtil.isNull(userAndTeam)) throw new ServiceException(Code.NOT_FOUND, "用户未加入小队");
        userAndTeamMapper.delete(new QueryWrapper<UserAndTeam>().eq("user_id", userId));
        user.setTeamId(null);
        userMapper.updateUserAll(user);
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
        List<Quest> quests = questMapper.selectList(new QueryWrapper<Quest>().eq("resolving_by_team_id", teamId));
        for (Quest quest : quests) {
            quest.setResolvingByTeamId(null);
            questMapper.updateQuest(quest);
        }

        userAndTeamMapper.delete(new QueryWrapper<UserAndTeam>().eq("team_id", teamId));

        return teamMapper.deleteTeamById(teamId)>0;
    }

    @Override
    @Level(allowLevel = 5)
    public PageInfo<Team> findTeamByConditions(TeamParam teamParam) {
        PageHelper.startPage(teamParam.getPageNum(),teamParam.getPageSize());
        List<Team> teamList = teamMapper.findTeamByConditions(teamParam);
        return PageInfo.of(teamList);
    }

    @Override
    @Level(allowLevel = 5)
    public List< User> findTeamMember(Long teamId) {
        Team team = teamMapper.findTeamById(teamId);
        if (team == null) throw new ServiceException(Code.NOT_FOUND, "小队不存在");
        return userMapper.findUserByTeamId(teamId);
    }

}