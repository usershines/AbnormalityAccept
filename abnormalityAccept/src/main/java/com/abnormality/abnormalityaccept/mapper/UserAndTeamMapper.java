package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.UserAndTeam;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:此文件用来实现用户和小队数据表的关联性问题 }
 * @since 2025-07-20
 */
@Mapper
public interface UserAndTeamMapper extends BaseMapper<UserAndTeam> {

}
