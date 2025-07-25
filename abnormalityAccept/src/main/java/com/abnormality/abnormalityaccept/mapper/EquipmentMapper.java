
package com.abnormality.abnormalityaccept.mapper;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.abnormality.abnormalityaccept.entity.param.EquipmentParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
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
    // ============== 拓展方法 ==============

    /**
     * 统计装备数量
     * @return 装备总数
     */
    int countEquipment(@Param("state") Integer state,
                       @Param("type") String type);

    /**
     * 批量更新装备状态
     * @param ids 装备ID列表
     * @param state 新状态
     * @return 更新条数
     */
    int batchUpdateEquipmentState(@Param("ids") List<Long> ids,@Param("state") Integer state);

    /**
     * 批量删除装备
     * @param ids 装备ID列表
     * @return 删除条数
     */
    int batchDeleteEquipment(@Param("ids") List<Long> ids);

    List<Equipment> findEquipmentByConditions (EquipmentParam equipmentParam);


}
