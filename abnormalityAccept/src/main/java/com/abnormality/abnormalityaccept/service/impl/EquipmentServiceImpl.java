package com.abnormality.abnormalityaccept.service.impl;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.abnormality.abnormalityaccept.entity.param.EquipmentParam;
import com.abnormality.abnormalityaccept.service.EquipmentService;
import com.abnormality.abnormalityaccept.mapper.EquipmentMapper;

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
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentMapper equipmentMapper;
    @Override
    public PageInfo<Equipment> findAllEquipment(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Equipment> EquipmentList = equipmentMapper.findAllEquipment();
//        return PageInfo.of(EquipmentList);
        return new PageInfo<>(EquipmentList);
    }


    @Override
    public Equipment findEquipmentById(Long id) {
        return equipmentMapper.findEquipmentById(id);
    }

    /**
     *
     * 添加学生
     */
    @Override
    public boolean addEquipment(Equipment equipment) {

        return equipmentMapper.insertOrUpdate( equipment);

    }
    /**
     *修改学生
     */
    @Override
    public boolean updateEquipment(Equipment equipment){
//        return equipmentMapper.updateEquipment(equipment)>0;
        return equipmentMapper.insertOrUpdate( equipment);
    }

    /**
     *删除学生
     */
    @Override
    public boolean deleteEquipmentById(Long id){
        return equipmentMapper.deleteEquipmentById(id);
    }

    // 拓展方法实现


    @Override
    public int count() {
        return equipmentMapper.countEquipment();
    }

    @Override
    public boolean batchUpdateState(List<Long> ids, String state) {
        return equipmentMapper.batchUpdateEquipmentState(ids, state) > 0;
    }

    @Override
    public boolean batchDelete(List<Long> ids) {
        return equipmentMapper.batchDeleteEquipment(ids) > 0;
    }

    @Override
    public PageInfo<Equipment> findEquipmentByConditions(EquipmentParam equipmentParam) {
        PageHelper.startPage(equipmentParam.getPageNUm(),equipmentParam.getPageSize());
        List<Equipment> EquipmentList = equipmentMapper.findEquipmentByConditions(equipmentParam);
        return new PageInfo<>(EquipmentList);
    }






}
