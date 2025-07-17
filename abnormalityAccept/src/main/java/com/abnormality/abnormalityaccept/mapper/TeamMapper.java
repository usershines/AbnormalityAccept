package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Team;
import org.apache.ibatis.annotations.Mapper;

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

    int deleteTeamByName(String name);


}
