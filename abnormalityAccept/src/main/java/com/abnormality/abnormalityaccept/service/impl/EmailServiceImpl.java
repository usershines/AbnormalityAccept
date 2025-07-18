package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.entity.Email;
import com.abnormality.abnormalityaccept.mapper.EmailMapper;
import com.abnormality.abnormalityaccept.service.EmailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailMapper emailMapper;

    @Override
    public PageInfo<Email> findAllEmail(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return PageInfo.of(emailMapper.findAllEmail());
    }
    @Override
    public Email findEmailById(Long id) {
        return emailMapper.findEmailById(id);
    }

    @Override
    public boolean addEmail(Email email) {
        return emailMapper.addEmail(email);
    }
    @Override
    public boolean updateEmail(Email email) {
        return emailMapper.updateEmail(email);
    }
    @Override
    public boolean deleteEmailById(Long id) {
        return emailMapper.deleteEmailById(id);
    }
    @Override
    public PageInfo<Email> findEmailBySenderId(Long senderId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Email> emailList = emailMapper.findEmailBySenderId(senderId);
        return PageInfo.of(emailList);
    }

    @Override
    public PageInfo<Email> findEmailByReceiverId(Long receiverId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Email> emailList = emailMapper.findEmailByReceiverId(receiverId);
        return PageInfo.of(emailList);
    }

    @Override
    public PageInfo<Email> findEmailByTheme(String theme, Long UserId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Email> emailList = emailMapper.findEmailByTheme(theme, UserId);
        return PageInfo.of(emailList);
    }

}
