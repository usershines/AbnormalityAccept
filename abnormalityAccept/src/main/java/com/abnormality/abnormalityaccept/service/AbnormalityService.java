package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.AbnormalityParam;
import com.abnormality.abnormalityaccept.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface AbnormalityService {

    PageInfo<Abnormality> findAllAbnormality(Integer pageNum, Integer pageSize);

    Abnormality findAbnormalityById(Long id);

    // 创建新的异想体记录
    boolean addAbnormality(Abnormality abnormality);

    // 更新异想体信息
    boolean updateAbnormality( Abnormality updatedData,Long editorId);

    // 查询异想体（多条件）
    PageInfo<Abnormality> findAbnormalityByConditions(AbnormalityParam abnormalityParam, Integer pageNum, Integer pageSize);

//    // 获取异想体详情
//    Abnormality getAbnormalityDetails(Long id);
//
//
//    // 转移异想体到新设施
//    void transferAbnormality(Long abnormalityId, Long newFacilityId);
}
