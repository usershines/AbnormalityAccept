package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.dto.request.EmailAddRequest;
import com.abnormality.abnormalityaccept.entity.Email;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.EmailMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.EmailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public  final UserMapper userMapper;


    @Override
    public PageInfo<Email> findAllEmail(Integer pageNum, Integer pageSize, Long receiverId) {
        PageHelper.startPage(pageNum,pageSize);
        return PageInfo.of(emailMapper.findAllEmail(receiverId));
    }
    @Override
    public Email findEmailById(Long id, Long receiverId) {
        return emailMapper.findEmailById(id, receiverId);
    }

    @Override
    public boolean sendEmail(EmailAddRequest emailAddRequest, Long userID) {
        Email email = new Email();
        if(emailAddRequest.getTheme() == null) throw new ServiceException("邮件主题不能为空");
        if (emailAddRequest.getReceiverName() == null) throw new ServiceException("接收者用户名不能为空");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username",emailAddRequest.getReceiverName()));
        if(user == null) throw new ServiceException("接收者用户不存在");
        Long receiverId = user.getId();
        email.setSenderId(userID);
        email.setSenderName(userMapper.selectById(userID).getUsername());
        email.setReceiverId(receiverId);
        email.setReceiverName(user.getUsername());
        email.setTheme(emailAddRequest.getTheme());
        email.setContent(emailAddRequest.getContent());
        email.setSendTime(LocalDate.now());
        email.setState(0);
        return emailMapper.addEmail(email);
    }

    @Override
    public boolean deleteEmailById(Long id, Long receiverId) {
        return emailMapper.deleteEmailById(id, receiverId);
    }
    @Override
    public PageInfo<Email> findEmailBySenderId(Long senderId, Integer pageNum, Integer pageSize, Long receiverId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Email> emailList = emailMapper.findEmailBySenderId(senderId, receiverId);
        return PageInfo.of(emailList);
    }


    @Override
    public PageInfo<Email> findEmailByTheme(String theme, Integer pageNum, Integer pageSize, Long receiverId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Email> emailList = emailMapper.findEmailByTheme(theme, receiverId);
        return PageInfo.of(emailList);
    }

    @Override
    public boolean updateEmailState(Long id , Integer state, Long receiverId) {
        Email email = emailMapper.findEmailById(id, receiverId);
        if(state != 1 && state != 0) throw new RuntimeException("状态值错误");
        else {
            email.setState(state);
            return true;
        }
    }
}
