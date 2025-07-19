package com.abnormality.abnormalityaccept.service;
import com.abnormality.abnormalityaccept.entity.Facility;
import com.abnormality.abnormalityaccept.entity.param.FacilityParam;
import com.github.pagehelper.PageInfo;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface FacilityService {
    PageInfo<Facility> findAllFacility(Integer pageNum, Integer pageSize);
    Facility findFacilityById(Long id);
    boolean addFacility(Facility facility);
    boolean updateFacility(Facility facility);
    boolean deleteFacilityById(Long id);

    PageInfo<Facility> findFacilityByConditions(FacilityParam facilityParam);

}
