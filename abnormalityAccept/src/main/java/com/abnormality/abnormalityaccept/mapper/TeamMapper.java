package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.TeamParam;
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
public interface TeamMapper extends BaseMapper<Team> {

    // teamMapper.selectById()

    List<Team> findAllTeam();

    Team findTeamById(Long id);

    Team findTeamByResolvingQuestId(Long resolvingQuestId);

    Team findTeamByName(String name);

    int addTeam(Team team);

    int updateTeam(Team team);

    int deleteTeamById(Long id);

    int updateQuestId(@Param("teamId") Long teamId, @Param("questId") Long questId); // 更新任务ID

    int updateStatus(@Param("teamId") Long teamId, @Param("status") String status); // 更新状态

    List<Team> findTeamByConditions(TeamParam teamParam);

    List<Team> findTeamLeisure();

}
