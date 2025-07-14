package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Facility;
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
public interface FacilityMapper {

    // 多条件查询设施
    List<Facility> searchFacilities(
            @Param("id") Long id,
            @Param("facilityName") String facilityName,
            @Param("level") Integer level
    );

    // 查询设施及其包含的异想体
    Facility findFacilityWithAbnormalities(@Param("id") Long id);
}
