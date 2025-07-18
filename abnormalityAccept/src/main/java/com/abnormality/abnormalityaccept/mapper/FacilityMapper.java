package com.abnormality.abnormalityaccept.mapper;
import com.abnormality.abnormalityaccept.entity.Facility;
import com.abnormality.abnormalityaccept.entity.param.FacilityParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface FacilityMapper extends BaseMapper<Facility> {
    /**
     *查询所有设施
     */
    List<Facility> findAllFacility(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize );
    /**
     根据id查询设施
     */
    Facility findFacilityById(Long id);
    /**
     *添加设施
     */
    int addFacility(Facility facility);
    /**
     *修改设施
     */
    int updateFacility(Facility facility);
    /**
     *删除设施
     */
    boolean deleteFacilityById(Long id);

    List<Facility> findFacilityByConditions(FacilityParam facilityParam);


}
