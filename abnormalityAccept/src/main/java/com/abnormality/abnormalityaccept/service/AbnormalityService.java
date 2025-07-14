package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Abnormality;
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
    Abnormality addAbnormality(Abnormality abnormality, User creator);

    // 更新异想体信息
    Abnormality updateAbnormality(Long id, Abnormality updatedData, User updater);

    // 查询异想体（多条件）
    PageInfo<Abnormality> searchAbnormalities(
            Long id, String name, Integer level, Long facilityId);

    // 获取异想体详情
    Abnormality getAbnormalityDetails(Long id);

    // 无效化异想体（标记为无效）
    void deactivateAbnormality(Long id);

    // 转移异想体到新设施
    void transferAbnormality(Long abnormalityId, Long newFacilityId);
}
