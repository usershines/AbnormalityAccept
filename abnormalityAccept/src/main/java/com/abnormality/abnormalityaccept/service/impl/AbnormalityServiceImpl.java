package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.param.AbnormalityParam;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.AbnormalityMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    private  final UserMapper userMapper;

    @Override
    @DS("slave")
    public PageInfo<Abnormality> findAllAbnormality(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Abnormality> abnormalityList = abnormalityMapper.selectList(null);
        return PageInfo.of(abnormalityList);
    }

    @Override
    @Cacheable(value = "abnormality",key = "#id")
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
        return abnormalityMapper.insertOrUpdate(abnormality);
    }


    //根据用户等级获取相应的修改权限
    @Override
    public boolean updateAbnormality( Abnormality updatedData,Long editorId) {
        User editor = userMapper.findUserById(editorId);
        if(updatedData.getLevel()>editor.getLevel())
            throw new ServiceException(Code.BAD_REQUEST, "不能修改等级比自己高的异想体信息");
        return  abnormalityMapper.updateAbnormality(updatedData);
    }

    @Override
    public PageInfo<Abnormality> findAbnormalityByConditions(AbnormalityParam abnormalityParam) {
        PageHelper.startPage(abnormalityParam.getPageNum(),abnormalityParam.getPageSize());
        List<Abnormality> abnormalityList = abnormalityMapper.findAbnormalityByConditions(abnormalityParam);
        return PageInfo.of(abnormalityList);
    }

    @Override
    public PageInfo<Abnormality> findAbnormalityByFacilityId(Long facilityId, Integer pageNum, Integer pageSize) {
        if(ObjectUtil.isEmpty(pageNum)){
            pageNum = 1;
        }
        if(ObjectUtil.isEmpty(pageSize)){
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Abnormality> abnormalityList = abnormalityMapper.selectList(new QueryWrapper<Abnormality>().eq("facility_id", facilityId));
        return PageInfo.of(abnormalityList);
    }


}
