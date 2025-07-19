package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.param.AbnormalityParam;
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


    List<Abnormality> findAllAbnormality(Integer pageNum, Integer pageSize);

    Abnormality findAbnormalityById(Long id);

    boolean addAbnormality(Abnormality abnormality);

    boolean updateAbnormality(Abnormality abnormality);

    List<Abnormality> findAbnormalityByConditions(AbnormalityParam abnormalityParam);





}
