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
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.exception.BaseException;
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

//    @Override
//    @Level(allowLevel = {5})
//    public Team createTeam(Team team) {
//
//    }

//    @Override
//    @Level(allowLevel = {5})
//    public Team updateTeam(Long teamId, Team updatedData) {
//
//    }

}