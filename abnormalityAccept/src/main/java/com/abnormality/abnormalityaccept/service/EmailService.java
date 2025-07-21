package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.dto.request.EmailAddRequest;
import com.abnormality.abnormalityaccept.entity.Email;
import com.github.pagehelper.PageInfo;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface EmailService {
    PageInfo<Email> findAllEmail( Integer pageNum, Integer pageSize ,Long receiverId);

    Email findEmailById(Long id, Long receiverId);

    //发送邮件
    boolean sendEmail(EmailAddRequest emailAddRequest, Long userId);


    boolean deleteEmailById(Long id, Long receiverId);

    PageInfo<Email> findEmailBySenderId(Long senderId, Integer pageNum, Integer pageSize, Long receiverId);


    PageInfo<Email> findEmailByTheme(String theme  , Integer pageNum, Integer pageSize, Long receiverId);

    boolean updateEmailState(Long id, Integer state, Long receiverId);

    //查询自己发送过的邮件
    PageInfo<Email> findEmailOneself(Long Id, Integer pageNum, Integer pageSize);

    //统计未读邮件数量
    Integer countUnreadEmail(Long receiverId);

    //一键已读功能
    boolean readAllEmail(Long receiverId);

}
