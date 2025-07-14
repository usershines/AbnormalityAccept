package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
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
public interface AbnormalityMapper extends BaseMapper<Abnormality> {


    List<Abnormality> findAllAbnormality();

    Abnormality findAbnormalityById(Long id);

    boolean addAbnormality(Abnormality abnormality);

    boolean updateAbnormality(Abnormality abnormality);

    List<Abnormality> findAbnormalityByConditions(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("level") Integer level,
            @Param("facilityId") Long facilityId
    );



}
