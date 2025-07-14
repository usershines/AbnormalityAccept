package com.abnormality.abnormalityaccept.service;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.github.pagehelper.PageInfo;
/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface EquipmentService {
    PageInfo<Equipment> findAllEquipment(Integer pageNum, Integer pageSize);
    Equipment findEquipmentById(Long id);

    boolean addEquipment(Equipment equipment);
    boolean updateEquipment(Equipment equipment);
    boolean deleteEquipmentById(Long id);
}
