package com.abnormality.abnormalityaccept.mapper;

import com.abnormality.abnormalityaccept.entity.Email;
import com.abnormality.abnormalityaccept.entity.User;
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

    List<Email> findAllEmail();

    Email findEmailById(Long id);

    boolean addEmail(Email email);

    boolean updateEmail(Email email);

    boolean deleteEmailById(Long id);

    //boolean groupEmailByReceiverId(Long receiverId);


    Email findEmailBySenderId(Long senderId);



}
