package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Email;
import com.abnormality.abnormalityaccept.service.EmailService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageHelper;
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
@RequestMapping("/user/email")
@Tag(name = "邮件管理")
public class EmailController {

    private final EmailService emailService;

    private final UserService userService;

    @Operation(summary = "邮件查询")
    @GetMapping("/list")
    public Result<PageInfo<Email>> findAllEmail(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        PageInfo<Email> emailList = emailService.findAllEmail(pageNum,pageSize);
        return Result.ok(emailList);
    }

    @Operation(summary = "根据id查询邮件")
    @GetMapping("/{id}")
    public Result<Email> findEmailById(@PathVariable Long id){
        Email email = emailService.findEmailById(id);
        return email == null ? Result.error("未找到该邮件") : Result.ok(email);
    }

    @Operation(summary = "添加邮件")
    @PostMapping("/add")
    public Result<String> addEmail(@RequestBody Email email){
        boolean add = emailService.addEmail(email);
        return add ? Result.ok("添加成功") : Result.error("添加失败");
    }

    @Operation(summary = "更新邮件")
    @PutMapping("/update")
    public Result<String> updateEmail(@RequestBody Email email){
        boolean update = emailService.updateEmail(email);
        return update ? Result.ok("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除邮件")
    @DeleteMapping("/{id}")
    public Result<String> deleteEmailById(@PathVariable Long id){
        boolean delete = emailService.deleteEmailById(id);
        return delete ? Result.ok("删除成功") : Result.error("删除失败");
    }
    @Operation(summary = "根据发送者id查询邮件")
    @GetMapping("/sender/{senderId}")
    public Result<PageInfo<Email>> findEmailBySenderId(@PathVariable Long senderId,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo<Email> emailList = emailService.findEmailBySenderId(senderId,pageNum,pageSize);
        return Result.ok(emailList);
    }
    @Operation(summary = "根据接收者id查询邮件")
    @GetMapping("/receiver/")
    public Result<PageInfo<Email>> findEmailByReceiverId(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long receiverId = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailByReceiverId(receiverId,pageNum,pageSize);
        return Result.ok(emailList);
    }

    @Operation(summary = "根据主题查询邮件")
    @GetMapping("/receiver/theme")
    public Result<PageInfo<Email>> findEmailByTheme(@RequestParam String theme, @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Long userId = userService.getUserIdByToken();
        PageInfo<Email> emailList = emailService.findEmailByTheme(theme,userId,pageNum,pageSize);
        return Result.ok(emailList);
    }

}
