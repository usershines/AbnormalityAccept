package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.abnormality.abnormalityaccept.dto.request.QuestRequest;
import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.QuestParam;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.QuestService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestMapper questMapper;

    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addQuest(QuestRequest questRequest, Long sendId) {
        if(questRequest.getQuestCode() == null || questRequest.getQuestCode().isEmpty()) throw new IllegalArgumentException("任务代号不能为空");
        Quest quest = questMapper.findQuestByQuestCode(questRequest.getQuestCode());
        if(quest != null || ObjectUtil.isNotEmpty( quest)) throw new IllegalArgumentException("任务代号已存在");
        if(userMapper.findUserById(sendId).getLevel()< 4) throw new IllegalArgumentException("只有A级以上用户可以发布任务");

        questMapper.addQuest(questRequest);

        Quest newQuest = questMapper.findQuestById(questMapper.findQuestByQuestCode(questRequest.getQuestCode()).getId());
        Team team = teamMapper.findTeamByName(questRequest.getResolvingByTeamName());
        team.setResolvingQuestCode(newQuest.getQuestCode());
        team.setResolvingQuestId(newQuest.getId());
        team.setStatus(1);
        return teamMapper.updateTeam(team) > 0;
    }
    /**
     * 分页查询所有通知
     */
    @Override
    public PageInfo<Quest> findAllQuest(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Quest> questList = questMapper.findAllQuest(pageNum, pageSize);
        return PageInfo.of(questList);
    }
    /**
     * 根据ID查询通知
     */
    @Override
    public Quest findQuestById(Long id) {
        return questMapper.findQuestById(id);
    }

    /**
     * 更新通知
     */
    @Override
    public boolean updateQuest(Quest quest) {
        if(quest.getResolvingByTeamName()==null) {
            Team team = teamMapper.findTeamByResolvingQuestId(quest.getId());
            team.setResolvingQuestCode(null);
            team.setResolvingQuestId(null);
            if(team.getStatus()==0) throw new IllegalArgumentException("数据异常");
            if(team.getStatus()==1) team.setStatus(0);
            teamMapper.updateTeam( team);
        }
        else{
            //旧小队相关
            Team team = teamMapper.findTeamByResolvingQuestId(quest.getId());
            team.setResolvingQuestCode(null);
            team.setResolvingQuestId(null);
            if(team.getStatus()==0) throw new IllegalArgumentException("数据异常");
            if(team.getStatus()==1) team.setStatus(0);
            teamMapper.updateTeam(team);
            //新小队相关
            Team newTeam = teamMapper.findTeamByName(quest.getResolvingByTeamName());
            newTeam.setResolvingQuestId(quest.getId());
            newTeam.setResolvingQuestCode(quest.getQuestCode());
            newTeam.setStatus(1);
            teamMapper.updateTeam(newTeam);
        }


        List<Team> teamList = teamMapper.selectList(new QueryWrapper<Team>().eq("resolving_quest_id", quest.getId()));
        for(Team team:teamList){
            team.setResolvingQuestCode(quest.getQuestCode());
            teamMapper.updateTeam(team);
        }
        return questMapper.updateQuest(quest) > 0;
    }

    /**
     * 根据ID删除通知
     */
    @Override
    public boolean deleteQuestById(Long id) {
        Team team = teamMapper.findTeamByResolvingQuestId(id);
        if(team != null){
            team.setResolvingQuestId(null);
            team.setResolvingQuestCode(null);
            teamMapper.updateTeam(team);
        }
        return questMapper.deleteQuestById(id);
    }

    @Override
    public PageInfo<Quest> findQuestByConditions(QuestParam questParam) {
        PageHelper.startPage(questParam.getPageNum(),questParam.getPageSize());
        List<Quest> questList = questMapper.findQuestByConditions(questParam);
        return PageInfo.of(questList);
    }



}
