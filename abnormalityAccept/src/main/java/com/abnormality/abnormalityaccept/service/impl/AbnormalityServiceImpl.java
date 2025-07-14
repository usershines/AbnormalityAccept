package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.mapper.AbnormalityMapper;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
public class AbnormalityServiceImpl implements AbnormalityService {

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
    public Abnormality addAbnormality(Abnormality abnormality, User creator) {
        int userLevel = getUserLevel(creator);
        if (compareLevel(userLevel, abnormality.getLevel())) {

        }
    }

    //根据用户等级获取相应的修改权限
    @Override
    public Abnormality updateAbnormality(Long id, Abnormality updatedData, User updater) {

        // 获取当前异想体
        Abnormality existing = abnormalityRepository.findById(abnormalityId)
                .orElseThrow(() -> new EntityNotFoundException("异想体不存在"));

        // 验证用户是否有基本修改权限
        validateBasicPermission(existing, currentUser);

        // 特殊字段权限验证（等级和名称）
        validateSpecialFields(existing, updatedData, currentUser);

        // 更新允许修改的字段
        updateAllowedFields(existing, updatedData);

        return abnormalityRepository.save(existing);
    }

    //获取用户等级权限
    int getUserLevel(User user) {
        return user.getLevel();
    }

    //等级比较
    boolean compareLevel(int level1, int level2) {
        return level1 > level2;
    }
}
