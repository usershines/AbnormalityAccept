package com.abnormality.abnormalityaccept.service;
import com.abnormality.abnormalityaccept.entity.Notice;
import com.github.pagehelper.PageInfo;


/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface NoticeService {
    PageInfo<Notice> findAllNotice(Integer pageNum, Integer pageSize);
    Notice findNoticeById(Long id);
    boolean addNotice(Notice notice);
    boolean updateNotice(Notice notice);
    boolean deleteNoticeById(Long id);
}
