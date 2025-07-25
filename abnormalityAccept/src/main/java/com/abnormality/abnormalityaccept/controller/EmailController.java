package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.EmailAddRequest;
import com.abnormality.abnormalityaccept.entity.Email;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.service.EmailService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.abnormality.abnormalityaccept.util.AopUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "邮件管理")
public class EmailController {

    private final EmailService emailService;

    private final UserService userService;

    @Operation(summary = "邮件查询")
    @GetMapping("/list")
    public Result<PageInfo<Email>> findAllEmail(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long receiverId = userService.getUserIdByToken();
        String  username = JwtPayload.fromToken(AopUtil.getToken()).getUsername();
        PageInfo<Email> emailList = emailService.findAllEmail(pageNum,pageSize,receiverId);
        if(emailList.getList() == null || emailList.getList().isEmpty()) return Result.error("您暂时没有收到过邮件");
        return Result.ok(emailList);
    }

    @Operation(summary = "根据id查询邮件")
    @GetMapping("/{id}")
    public Result<Email> findEmailById(@PathVariable Long id){
        Long receiverId = userService.getUserIdByToken();
        Email email = emailService.findEmailById(id,receiverId);
        return email == null ? Result.error("未找到该邮件") : Result.ok(email);
    }

    @Operation(summary = "发送邮件")
    @PostMapping("/send")
    public Result<String> sendEmail(@RequestBody EmailAddRequest emailAddRequest){
        Long userId = userService.getUserIdByToken();
        boolean send = emailService.sendEmail(emailAddRequest,userId);
        return send ? Result.ok( "发送成功") : Result.error(Code.ERROR.getCode(),"发送失败");
    }


    @Operation(summary = "删除邮件")
    @DeleteMapping("/{id}")
    public Result<String> deleteEmailById(@PathVariable Long id){
        Long receiverId = userService.getUserIdByToken();
        boolean delete = emailService.deleteEmailById(id,receiverId);
        return delete ? Result.ok("删除成功") : Result.error(Code.ERROR.getCode(),"删除失败");
    }
    @Operation(summary = "根据发送者名称查询邮件")
    @GetMapping("/sender/")
    public Result<PageInfo<Email>> findEmailBySender(@RequestParam String senderName,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long senderId =  userService.findUserByName(senderName).getId();
        Long receiverId =  userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailBySenderId(senderId,pageNum,pageSize,receiverId);
        return Result.ok(emailList);
    }

    @Operation(summary = "根据主题查询邮件")
    @GetMapping("/theme")
    public Result<PageInfo<Email>> findEmailByTheme(@RequestParam String theme, @RequestParam Integer pageNum, @RequestParam Integer pageSize){

        Long receiverId = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailByTheme(theme,pageNum,pageSize,receiverId);
        return Result.ok(emailList);
    }

    @Operation(summary = "更改邮件状态")
    @PutMapping("/{id}/state")
    public Result<String> updateEmailState(@PathVariable Long id,@RequestParam Integer state){
        Long receiverId = userService.getUserIdByToken();
        boolean update = emailService.updateEmailState(id,state,receiverId);
        return update ? Result.ok("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "查询用户自己发送的文件")
    @GetMapping("/history")
    public Result<PageInfo<Email>> findEmailOneself(@RequestParam Integer pageNum,@RequestParam Integer pageSize ){
        Long Id = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailOneself(Id,pageNum,pageSize);
        if(emailList.getList()==null||emailList.getList().isEmpty()) return Result.error(Code.NOT_FOUND.getCode(),"没有发送的文件");
        return Result.ok(emailList);
    }

    @Operation(summary = "统计未读邮件数目")
    @GetMapping("/countUnRead")
    public Result<Integer> countUnreadEmail() {
        Long receiverId = userService.getUserIdByToken();
        Integer count = emailService.countUnreadEmail(receiverId);
        return Result.ok(count);
    }

    @Operation(summary = "一键已读")
    @GetMapping("/readAll")
    public Result<String> readAllEmail() {
        Long receiverId = userService.getUserIdByToken();
        boolean result = emailService.readAllEmail(receiverId);
        return result ? Result.ok("已读成功") : Result.error("已读失败");
    }

    @Operation(summary = "根据状态查询邮件")
    @GetMapping("/state")
    public Result<PageInfo<Email>> findEmailByState(@RequestParam Integer state,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long receiverId = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailByState(state,pageNum,pageSize,receiverId);
        return Result.ok(emailList);
    }
    // 根据发送者等级查询邮件
    @Operation(summary = "根据发送者等级查询邮件")
    @GetMapping("/senderLevel")
    public Result<PageInfo<Email>> findEmailBySenderLevel(@RequestParam Integer level,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long receiverId = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailBySenderLevel(level,pageNum,pageSize,receiverId);
        return Result.ok(emailList);
    }

}
