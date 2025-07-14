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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Result<String> inviteUser(
            @Valid @RequestBody InviteRequest request,
            BindingResult bindingResult,
            @RequestHeader("Authorization") String token) {

        try {
            // 1. 验证请求参数
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("; "));
                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
            }

            // 2. 验证当前用户权限
            if (!userService.verify(token)) {
                throw new ServiceException(Code.NOT_AUTHORIZED, "未认证，请先登录");
            }

            // 3. 从token中获取邀请人ID
            JWT jwt = JWT.of(token.replace("Bearer ", ""));
            Long inviterId = Long.valueOf(jwt.getPayload("userId").toString());

            // 4. 创建新用户对象
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setFacilityId(request.getFacilityId());
            newUser.setLevel(request.getLevel());

            // 5. 调用服务添加用户
            boolean result = userService.addUser(newUser, inviterId);

            if (result) {
                return Result.ok("用户创建成功，初始密码为: " + request.getUsername() + "123");
            } else {
                throw new ServiceException(Code.ERROR, "用户创建失败");
            }
        } catch ( BaseException e) {
            // 业务异常或基础异常
            return Result.error(e.getCode().getCode(), e.getMessage());
        } catch (Exception e) {
            // 系统异常
            log.error("邀请用户时发生系统错误", e);
            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
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

    // 邀请请求DTO
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


    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody AuthRequest req) {
        return Result.ok(userService.login(req.getName(), req.getPassword()));
    }

}