package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.AbnormalityMapper;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.baomidou.dynamic.datasource.annotation.DS;
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
    @DS("slave")
    public PageInfo<Abnormality> findAllAbnormality(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Abnormality> abnormalityList = abnormalityMapper.findAllAbnormality(pageNum, pageSize);
        return PageInfo.of(abnormalityList);
    }

    @Override
    public Abnormality findAbnormalityById(Long id) {
        Abnormality abnormality = abnormalityMapper.findAbnormalityById(id);
        if(ObjectUtil.isEmpty(abnormality)){
            throw new ServiceException(Code.NOT_FOUND,"异想体不存在");
        }
        return abnormality;
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
        List<Abnormality> abnormalityList = abnormalityMapper.findAbnormalityByConditions(abnormality);
        return PageInfo.of(abnormalityList);
    }


}
