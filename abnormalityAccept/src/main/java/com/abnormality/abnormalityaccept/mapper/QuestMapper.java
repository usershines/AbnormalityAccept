package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.dto.request.QuestRequest;
import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.entity.param.QuestParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface QuestMapper extends BaseMapper<Quest> {
    /**
     * 查询所有任务
     */
    List<Quest> findAllQuest(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize );

    /**
     * 根据id查询任务
     */
    Quest findQuestById(Long id);
    boolean addQuest(QuestRequest  qre);

    /**
     * 修改任务
     */
    int updateQuest(Quest quest);

    /**
     * 删除任务
     */
    boolean deleteQuestById(Long id);

    // ============== 拓展方法 ==============
    List<Quest> findQuestByConditions(QuestParam questParam);
//
//    /**
//     * 根据状态查询任务
//     * @param state 任务状态
//     * @return 任务列表
//     */
//    List<Quest> findQuestByState(String state);
//
//    /**
//     * 统计任务数量
//     * @return 任务总数
//     */
//    int countQuest();
//
//    /**
//     * 批量更新任务状态
//     * @param ids 任务ID列表
//     * @param state 新状态
//     * @return 更新条数
//     */
//    int batchUpdateQuestState(@Param("ids") List<Long> ids, @Param("state") String state);
//
//    /**
//     * 批量删除任务
//     * @param ids 任务ID列表
//     * @return 删除条数
//     */
//    int batchDeleteQuest(List<Long> ids);
//
//    /**
//     * 根据名称模糊查询任务
//     * @param name 任务名称（模糊匹配）
//     * @return 任务列表
//     */
//    List<Quest> findQuestByName(@Param("name") String name);
//

}
