package com.abnormality.abnormalityaccept.mapper;
import com.abnormality.abnormalityaccept.entity.Notice;
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
public interface NoticeMapper extends BaseMapper<Notice> {
    /**
     *查询所有公告
     */
    List<Notice> findAllNotice(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize );
    /**
     根据id查询公告
     */
    Notice findNoticeById(Long id);
    /**
     *添加公告
     */
    int addNotice(Notice notice);
    /**
     *修改公告
     */
    int updateNotice(Notice notice);
    /**
     *删除公告
     */
    boolean deleteNoticeById(Long id);

    List<Notice> findNoticeByConditions(Notice notice);

}
