package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

    Abnormality findAbnormalityByCode(String code);

    int addAbnormality(Abnormality abnormality);

    int setAbnormalityState(Long id);



}
