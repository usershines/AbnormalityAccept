package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.request.QuestRequest;
import com.abnormality.abnormalityaccept.entity.Notice;
import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.QuestParam;
import com.github.pagehelper.PageInfo;
import java.util.List;
/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */public interface QuestService {
     PageInfo<Quest> findAllQuest(Integer pageNum, Integer pageSize);
     Quest findQuestById(Long id);

     boolean updateQuest(Quest quest);

     boolean deleteQuestById(Long id);
     boolean addQuest(QuestRequest questRequest, Long sendId);
     PageInfo<Quest> findQuestByConditions(QuestParam questParam);


}
