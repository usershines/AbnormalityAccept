package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Email;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface EmailMapper extends BaseMapper<Email> {

    List<Email> findAllEmail(Long receiverId);

    Email findEmailById(Long id, Long receiverId);

    boolean addEmail(Email email);

    boolean deleteEmailById(Long id, Long receiverId);

    List< Email> findEmailByTheme(String theme ,Long receiverId);


    List<Email> findEmailBySenderId(Long senderId, Long receiverId);




}
