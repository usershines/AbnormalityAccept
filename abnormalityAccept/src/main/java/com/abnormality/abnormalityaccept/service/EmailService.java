package com.abnormality.abnormalityaccept.service;

import com.abnormality.abnormalityaccept.entity.Email;
import com.github.pagehelper.PageInfo;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface EmailService {
    PageInfo<Email> findAllEmail( Integer pageNum, Integer pageSize);

    Email findEmailById(Long id);

    boolean addEmail(Email email);

    boolean updateEmail(Email email);

    boolean deleteEmailById(Long id);

    PageInfo<Email> findEmailBySenderId(Long senderId, Integer pageNum, Integer pageSize);

    PageInfo<Email> findEmailByReceiverId(Long receiverId, Integer pageNum, Integer pageSize);

    PageInfo<Email> findEmailByTheme(String theme ,Long UserId , Integer pageNum, Integer pageSize);
}
