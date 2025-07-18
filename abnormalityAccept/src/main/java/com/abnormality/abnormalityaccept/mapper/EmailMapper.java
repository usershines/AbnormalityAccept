package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Email;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface EmailMapper {

    List<Email> findAllEmail(Long receiverId);

    Email findEmailById(Long id, Long receiverId);

    boolean addEmail(Email email);

    boolean deleteEmailById(Long id, Long receiverId);

    List< Email> findEmailByTheme(String theme ,Long receiverId);


    List<Email> findEmailBySenderId(Long senderId, Long receiverId);




}
