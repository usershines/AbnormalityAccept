package com.abnormality.abnormalityaccept.service.impl;
import com.abnormality.abnormalityaccept.entity.Notice;
import com.abnormality.abnormalityaccept.service.NoticeService;
import com.abnormality.abnormalityaccept.mapper.NoticeMapper;

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
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    /**
     * 分页查询所有通知
     */
    @Override
    public PageInfo<Notice> findAllNotice(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> noticeList = noticeMapper.findAllNotice(pageNum, pageSize);
        return PageInfo.of(noticeList);
    }

    /**
     * 根据ID查询通知
     */
    @Override
    public Notice findNoticeById(Long id) {
        return noticeMapper.findNoticeById(id);
    }

    /**
     * 添加通知
     */
    @Override
    public boolean addNotice(Notice notice) {
        return noticeMapper.addNotice(notice) > 0;
    }

    /**
     * 更新通知
     */
    @Override
    public boolean updateNotice(Notice notice) {
        return noticeMapper.updateNotice(notice) > 0;
    }

    /**
     * 根据ID删除通知
     */
    @Override
    public boolean deleteNoticeById(Long id) {
        return noticeMapper.deleteNoticeById(id);
    }

    @Override
    public PageInfo<Notice> findNoticeByCondition(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> noticeList = noticeMapper.findNoticeByConditions(notice);
        return PageInfo.of(noticeList);
    }

}
