package com.abnormality.abnormalityaccept.controller;
import cn.hutool.jwt.JWT;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.AuthRequest;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;



/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request.getUsername(), request.getPassword());
            return Result.ok(response);
        } catch (ServiceException | BaseException e) {
            return Result.error(e.getCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(Code.ERROR.getCode(), "登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isBLevelOrHigher()")
    public Result<String> inviteUser(
            @Valid @RequestBody InviteRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        try {
            // 1. 验证请求参数
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("; "));
                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
            }

            // 2. 获取当前用户ID
            Long inviterId = getCurrentUserId(authentication);

            // 3. 创建新用户对象
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setFacilityId(request.getFacilityId());
            newUser.setLevel(request.getLevel());

            // 4. 调用服务添加用户
            boolean result = userService.addUser(newUser, inviterId);

            if (result) {
                String message = String.format(
                        "用户创建成功! 用户名: %s, 初始密码: %s123",
                        request.getUsername(), request.getUsername()
                );
                return Result.ok(message);
            } else {
                throw new ServiceException(Code.ERROR, "用户创建失败");
            }
        } catch (ServiceException | BaseException e) {
            return Result.error(e.getCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("邀请用户失败", e);
            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            BindingResult bindingResult) {

        try {
            // 1. 验证请求参数
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("; "));
                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
            }

            // 2. 获取用户
            User user = userService.findUserById(id);
            if (user == null) {
                throw new ServiceException(Code.NOT_FOUND, "用户不存在");
            }

            // 3. 更新用户信息
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setLevel(request.getLevel());
            user.setFacilityId(request.getFacilityId());
            user.setIntroduction(request.getIntroduction());

            // 4. 如果有新密码则更新
            if (StringUtils.hasText(request.getNewPassword())) {
                user.setPassword(userService.encryptPassword(request.getNewPassword()));
            }

            // 5. 保存更新
            boolean result = userService.updateUser(user);

            if (result) {
                return Result.ok("用户信息更新成功");
            } else {
                throw new ServiceException(Code.ERROR, "用户信息更新失败");
            }
        } catch (ServiceException | BaseException e) {
            return Result.error(e.getCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
        }
    }


    @GetMapping("/verify")
    public Result<Boolean> verifyToken(@RequestParam String token) {
        try {
            boolean isValid = userService.verify(token);
            return Result.ok(isValid);
        } catch (Exception e) {
            log.error("令牌验证失败", e);
            return Result.error(Code.ERROR.getCode(), "令牌验证失败");
        }
    }

//    private Long getCurrentUserId(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ServiceException(Code.UNAUTHORIZED, "用户未登录");
//        }
//
//        // 从认证信息中获取用户ID
//        if (authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            return ((User) userDetails).getId();
//        } else if (authentication.getPrincipal() instanceof String) {
//            String username = (String) authentication.getPrincipal();
//            User user = userService.findUserByUsername(username);
//            if (user != null) {
//                return user.getId();
//            }
//        }
//
//        throw new ServiceException(Code.UNAUTHORIZED, "无法获取当前用户信息");
//    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ServiceException(Code.UNAUTHORIZED, "用户未登录");
        }

        String username = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        } else {
            throw new ServiceException(Code.UNAUTHORIZED, "不支持的Principal类型");
        }

        if (username == null) {
            throw new ServiceException(Code.UNAUTHORIZED, "无法解析用户名");
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        }

        return user.getId();
    }

    //http://localhost:8080/user/findAllUser
    @Operation(summary = "用户信息查询")
    @GetMapping("/List")
    public Result<PageInfo<User>> findAllUser(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        //return List.of();
        PageInfo<User> userList = userService.findAllUser(pageNum, pageSize);
        return Result.ok(userList);
    }

    //http://localhost:8080/user/findUserById?id=1
    @Operation(summary = "根据id查询用户")
    @Parameter(name="id",description = "用户id",required = true,example = "1")
    @GetMapping("/{id}")
    public Result<User>  findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if(user == null) {
            return Result.error(500,"用户不存在");
        }
        return Result.ok("查询成功", user);
    }

    /**
     * 删除数据
     */
    @Operation(summary = "删除用户")
    //@Parameter(name="id",description = "用户id",required = true,example = "1")
    @DeleteMapping("/{id}")
    public Result<String> deleteUserById(@PathVariable Long id){
        if(userService.deleteUserById(id)){
            return Result.ok("删除成功");
        }
        else {
            return Result.error(500,"删除失败");
        }
    }

    @Operation(summary = "添加用户")
    @PostMapping("/invite")
    public Result<String> inviteUser(@RequestBody User user){
        if(userService.addUser(user,user.getInviterId())){
            return Result.ok("添加成功");
        }
        else {
            return Result.error(500,"添加失败");
        }
    }


    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user){
        if(userService.updateUser(user)){
            return Result.ok("更新成功");
        }
        else {
            return Result.error(500,"更新失败");
        }
    }

    @Operation(summary = "多条件查询")
    @PostMapping("/findUserByConditions")
    public Result<PageInfo<User>> findUserByConditions(@RequestParam Long id,
                                                       @RequestParam String username,
                                                       @RequestParam String email,
                                                        @RequestParam Integer level,
                                                       @RequestParam Long facilityId,
                                                       @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setLevel(level);
        user.setFacilityId(facilityId);
        PageInfo<User> userList = userService.findUserByConditions(user,pageNum, pageSize);
        return Result.ok(userList);
    }


    // DTO类定义
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class InviteRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotNull(message = "设施ID不能为空")
        private Long facilityId;

        @NotNull(message = "用户等级不能为空")
        @Min(value = 1, message = "等级必须大于0")
        @Max(value = 5, message = "等级不能超过5")
        private Integer level;
    }

    @Data
    public static class UpdateUserRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @Email(message = "邮箱格式不正确")
        private String email;

        @NotNull(message = "用户等级不能为空")
        @Min(value = 1, message = "等级必须大于0")
        @Max(value = 5, message = "等级不能超过5")
        private Integer level;

        @NotNull(message = "设施ID不能为空")
        private Long facilityId;

        private String introduction;

        private String newPassword;
    }




//    @Operation(summary = "用户登录")
//    @PostMapping("/login")
//    public Result<AuthResponse> login(@RequestBody AuthRequest req) {
//        return Result.ok(userService.login(req.getName(), req.getPassword()));
//    }

}