package com.abnormality.abnormalityaccept.service.impl;

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
    public boolean addQuest(Quest quest, Long sendId) {
        if(quest.getQuestCode() == null || quest.getQuestCode().isEmpty()) throw new IllegalArgumentException("任务编号不能为空");
        if(userMapper.findUserById(sendId).getLevel()< 4) throw new IllegalArgumentException("只有A级用户可以发布任务");
        return questMapper.addQuest(quest);
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
        List<Team> teamList = teamMapper.selectList(new QueryWrapper<Team>().eq("resolving_quest_id", quest.getId()));
        for(Team team:teamList){
            team.setResolvingQuestName(quest.getQuestName());
            teamMapper.updateTeam(team);
        }
        return questMapper.updateQuest(quest) > 0;
    }

    /**
     * 根据ID删除通知
     */
    @Override
    public boolean deleteQuestById(Long id) {

        List<Team> teamList = teamMapper.selectList(new QueryWrapper<Team>().eq("resolving_quest_id", id));
        for(Team team:teamList){
            team.setResolvingQuestName(null);
            team.setResolvingQuestId(null);
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
