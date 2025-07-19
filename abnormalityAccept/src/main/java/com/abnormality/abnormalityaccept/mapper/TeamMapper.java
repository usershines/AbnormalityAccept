package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.TeamParam;
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
public interface TeamMapper {

    // teamMapper.selectById()

    List<Team> findAllTeam();

    Team findTeamById(Long id);

    Team findTeamByName(String name);

    int addTeam(Team team);

    int updateTeam(Team team);

    int deleteTeamById(Long id);

    int updateQuestId(@Param("teamId") Long teamId, @Param("questId") Long questId); // 更新任务ID

    int updateStatus(@Param("teamId") Long teamId, @Param("status") String status); // 更新状态

    List<Team> findTeamByConditions(TeamParam teamParam);

}
