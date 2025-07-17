package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.service.QuestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
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
    @Override
    public boolean addQuest(Quest quest, Integer sendId) {
        if(sendId <4) throw new IllegalArgumentException("只有A级用户可以发布任务");
        return questMapper.addQuest(quest);
    }
    /**
     * 分页查询所有通知
     */
    @Override
    public PageInfo<Quest> findAllQuest(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Quest> questList = questMapper.findAllQuests(pageNum, pageSize);
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
        return questMapper.updateQuest(quest) > 0;
    }

    /**
     * 根据ID删除通知
     */
    @Override
    public boolean deleteQuestById(Long id) {
        return questMapper.deleteQuestById(id);
    }

}
