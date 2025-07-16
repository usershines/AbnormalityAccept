package com.abnormality.abnormalityaccept.service;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.github.pagehelper.PageInfo;
/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
import java.util.List;
public interface EquipmentService {
    PageInfo<Equipment> findAllEquipment(Integer pageNum, Integer pageSize);
    Equipment findEquipmentById(Long id);

    boolean addEquipment(Equipment equipment);
    boolean updateEquipment(Equipment equipment);
    boolean deleteEquipmentById(Long id);
    // 拓展方法
    List<Equipment> findByState(String state);
    int count();
    boolean batchUpdateState(List<Long> ids, String state);
    boolean batchDelete(List<Long> ids);
    List<Equipment> findByName(String name);
//    int countEquipments(Integer type, Integer state);
}
