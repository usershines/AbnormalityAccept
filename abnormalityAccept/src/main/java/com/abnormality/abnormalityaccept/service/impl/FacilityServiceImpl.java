package com.abnormality.abnormalityaccept.service.impl;
import com.abnormality.abnormalityaccept.entity.Facility;
import com.abnormality.abnormalityaccept.entity.param.FacilityParam;
import com.abnormality.abnormalityaccept.service.FacilityService;
import com.abnormality.abnormalityaccept.mapper.FacilityMapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityMapper facilityMapper;

    /**
     * 分页查询所有设施
     */
    @Override
    public PageInfo<Facility> findAllFacility(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Facility> facilityList = facilityMapper.findAllFacility(pageNum, pageSize);
        return PageInfo.of(facilityList);
    }

    /**
     * 根据ID查询设施
     */
    @Override

    public Facility findFacilityById(Long id) {
        return facilityMapper.selectById(id);
    }

    /**
     * 添加设施
     */
    @Override
    @DS("slave")
    public boolean addFacility(Facility facility) {
        return facilityMapper.insertOrUpdate(facility);
    }

    /**
     * 更新设施信息
     */
    @Override
    public boolean updateFacility(Facility facility) {
        return facilityMapper.updateFacility(facility) > 0;
    }

    /**
     * 根据ID删除设施
     */
    @Override
    public boolean deleteFacilityById(Long id) {
        return facilityMapper.deleteFacilityById(id);
    }

    @Override
    public PageInfo<Facility>  findFacilityByConditions(FacilityParam facilityParam){
        PageHelper.startPage(facilityParam.getPageNum(),facilityParam.getPageSize());
        List<Facility> facilityList = facilityMapper.findFacilityByConditions(facilityParam);
        return PageInfo.of(facilityList);
    }

}
