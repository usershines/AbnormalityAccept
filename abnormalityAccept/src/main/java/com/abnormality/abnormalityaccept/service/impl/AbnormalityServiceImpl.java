package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.mapper.AbnormalityMapper;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
public class AbnormalityServiceImpl implements AbnormalityService {

    @Autowired
    private AbnormalityMapper abnormalityMapper;
    @Override
    public PageInfo<Abnormality> findAllAbnormality(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Abnormality> abnormalityList = abnormalityMapper.findAllAbnormality();
        return PageInfo.of(abnormalityList);
    }

    @Override
    public Abnormality findAbnormalityById(Long id) {
        return abnormalityMapper.findAbnormalityById(id);
    }


    //根据用户等级获取相应的添加权限
    @Override
    public boolean addAbnormality(Abnormality abnormality) {
        return abnormalityMapper.addAbnormality(abnormality);
    }


    //根据用户等级获取相应的修改权限
    @Override
    public boolean updateAbnormality( Abnormality updatedData) {
        return  abnormalityMapper.updateAbnormality(updatedData);
    }

    @Override
    public PageInfo<Abnormality> findAbnormalityByConditions(Abnormality abnormality,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Abnormality> abnormalityList = abnormalityMapper.findAbnormalityByConditions(abnormality.getId(),abnormality.getName(),abnormality.getLevel(),abnormality.getFacilityId());
        return PageInfo.of(abnormalityList);
    }

}
