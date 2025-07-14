
package com.abnormality.abnormalityaccept.mapper;
import com.abnormality.abnormalityaccept.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;



import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface EquipmentMapper {
    /**
     *查询所有装备
     */
    List<Equipment> findAllEquipment();
    /**
     根据id查询装备
     */
    Equipment findEquipmentById(Long id);
    /**
     *添加装备
     */
    int addEquipment(Equipment equipment);
    /**
     *修改装备
     */
    int updateEquipment(Equipment equipment);
    /**
     *删除装备
     */
    boolean deleteEquipmentById(Long id);
}
