package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.annotation.AuthIgnore;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.*;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.UserService;
import com.abnormality.abnormalityaccept.util.AopUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



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

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @AuthIgnore
    public Result<AuthResponse> register(@RequestBody AuthRequest req) {
        return Result.ok(userService.register(req.getName(), req.getPassword()));
    }

    @Operation(summary = "用户注册(优化)")
    @PostMapping("/regist")
    @AuthIgnore
    public Result<String> regist (@RequestBody @Validated RegistRequest req){
        if(userService.regist(req.getUsername(), req.getPassword(), req.getEmail()))
            return Result.ok("注册成功");
        return Result.error("注册失败");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @AuthIgnore
    public Result<AuthResponse> login(@RequestBody AuthRequest req) {
        //return Result.ok(userService.login(name,password));
        return Result.ok(userService.login(req.getName(), req.getPassword()));
    }
    
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    @AuthIgnore
    public Result<Boolean> logout() {
        String token = AopUtil.getToken();
        return Result.ok(userService.logout(token));
    }


    //http://localhost:8080/user/findAllUser
    @Operation(summary = "用户信息查询")
    @GetMapping("/list")
    public Result<PageInfo<User>> findAllUser(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        //return List.of();
        Long finderId = userService.getUserIdByToken();
        PageInfo<User> userList = userService.findAllUser(pageNum, pageSize,finderId);
        return Result.ok(userList);
    }

    //http://localhost:8080/user/findUserById?id=1
    @Operation(summary = "根据id查询用户")
    @Parameter(name="id",description = "用户id",required = true,example = "1" ,in= ParameterIn.PATH )
    @GetMapping("/{id}")
    public Result<User>  findUserById(@PathVariable Long id ) {
        String token = AopUtil.getToken();
        String  username = JwtPayload.fromToken(token).getUsername();
        Long finderId = userService.findUserByName(username).getId();
        User user = userService.findUserById(id,finderId);
        if(user == null) return Result.error(Code.ERROR.getCode(),"用户不存在");
        return Result.ok("查询成功", user);
    }


    /**
     * 删除数据
     */
    @Operation(summary = "删除用户")
    //@Parameter(name="id",description = "用户id",required = true,example = "1")
    @DeleteMapping("/{id}")
    public Result<String> deleteUserById(@PathVariable Long id){
        String token = AopUtil.getToken();
        String  username = JwtPayload.fromToken(token).getUsername();
        Long editorId = userService.findUserByName(username).getId();
        if(userService.deleteUserById(id,editorId)){
            return Result.ok("删除成功");
        }
        else return Result.error(Code.ERROR.getCode(),"删除失败");
    }

    @Operation(summary = "邀请用户")
    @PostMapping("/invite")
    public Result<String> inviteUser(@RequestBody InviteRequest inviteRequest){
        String token = AopUtil.getToken();
        String  username = JwtPayload.fromToken(token).getUsername();
        Long inviterId = userService.findUserByName(username).getId();
        if(userService.addUser(inviteRequest,inviterId)){
            return Result.ok("添加成功");
        }
        else return Result.error(Code.FORBIDDEN.getCode(),Code.FORBIDDEN.getMsg());
    }


    @Operation(summary = "更新用户本人信息")
    @PostMapping("/update")
    public Result<String> updateUser(@RequestBody UpdateUserOneSelfRequest updateUserOneSelfRequest){
        String token = AopUtil.getToken();
        String  username = JwtPayload.fromToken(token).getUsername();
        Long editorId = userService.findUserByName(username).getId();
        if(userService.updateUser(updateUserOneSelfRequest,editorId)){
            return Result.ok("更新成功");
        }
        return Result.error(Code.ERROR.getCode(),"更新失败");
    }

    @Operation(summary = "编辑下属信息")
    @PostMapping("/editSubordinate/{id}")
    public Result<String> editSubordinate(@PathVariable Long id, @RequestBody EditSubordinateRequest editSubordinateRequest){
        editSubordinateRequest.setSubordinateId(id);
        Long editorId = userService.getUserIdByToken();
        if(userService.editSubordinate(editSubordinateRequest,editorId)){
            return Result.ok("编辑成功");
        }
        return Result.error(Code.ERROR.getCode(),"编辑失败");
    }


    @Operation(summary = "多条件查询")
    @GetMapping("/conditions")
    public Result<PageInfo<User>> findUserByConditions(UserParamRequest userParamRequest) {

        PageInfo<User> userList = userService.findUserByConditions(userParamRequest);

        return Result.ok(userList);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest){
        String token = AopUtil.getToken();
        JwtPayload jwtPayload = JwtPayload.fromToken(token);
        Long userId = userService.findUserByName(jwtPayload.getUsername()).getId();
        if (userService.updatePassword(userId,updatePasswordRequest.getNewPassword()))
            return Result.ok("修改成功");
        else throw new ServiceException(Code.ERROR,"修改失败");
    }

    @Operation(summary = "根据名称查找")
    @GetMapping("/findByName")
    public Result<User> findUserByName(@RequestParam String name) {
        return Result.ok(userService.findUserByName(name));
    }



}


//弃用
//    @PostMapping("/login")
//    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            AuthResponse response = userService.login(request.getUsername(), request.getPassword());
//            return Result.ok(response);
//        } catch ( BaseException e) {
//            return Result.error(e.getCode().getCode(), e.getMessage());
//        } catch (Exception e) {
//            log.error("登录失败", e);
//            return Result.error(Code.ERROR.getCode(), "登录失败: " + e.getMessage());
//        }
//    }

//    @PostMapping("/invite")
//    @PreAuthorize("hasRole('ADMIN') or @securityService.isBLevelOrHigher()")
//    public Result<String> inviteUser(
//            @Valid @RequestBody InviteRequest request,
//            BindingResult bindingResult,
//            Authentication authentication) {
//
//        try {
//            // 1. 验证请求参数
//            if (bindingResult.hasErrors()) {
//                String errorMsg = bindingResult.getFieldErrors().stream()
//                        .map(FieldError::getDefaultMessage)
//                        .collect(Collectors.joining("; "));
//                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
//            }
//
//            // 2. 获取当前用户ID
//            Long inviterId = getCurrentUserId(authentication);
//
//            // 3. 创建新用户对象
//            User newUser = new User();
//            newUser.setUsername(request.getUsername());
//            newUser.setFacilityId(request.getFacilityId());
//            newUser.setLevel(request.getLevel());
//
//            // 4. 调用服务添加用户
//            boolean result = userService.addUser(newUser, inviterId);
//
//            if (result) {
//                String message = String.format(
//                        "用户创建成功! 用户名: %s, 初始密码: %s123",
//                        request.getUsername(), request.getUsername()
//                );
//                return Result.ok(message);
//            } else {
//                throw new ServiceException(Code.ERROR, "用户创建失败");
//            }
//        } catch ( BaseException e) {
//            return Result.error(e.getCode().getCode(), e.getMessage());
//        } catch (Exception e) {
//            log.error("邀请用户失败", e);
//            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
//        }
//    }



//    @PutMapping("/{id}")
//    public Result<String> updateUser(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdateUserRequest request,
//            BindingResult bindingResult) {
//
//        try {
//            // 验证请求参数
//            if (bindingResult.hasErrors()) {
//                String errorMsg = bindingResult.getFieldErrors().stream()
//                        .map(FieldError::getDefaultMessage)
//                        .collect(Collectors.joining("; "));
//                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
//            }
//
//            // 更新用户基本信息
//            User user = new User();
//            user.setId(id);
//            user.setUsername(request.getUsername());
//            user.setEmail(request.getEmail());
//            user.setLevel(request.getLevel());
//            user.setFacilityId(request.getFacilityId());
//            user.setIntroduction(request.getIntroduction());
//
//            boolean result = userService.updateUser(user);
//
//            // 如果有新密码则更新
//            if (StringUtils.hasText(request.getNewPassword())) {
//                userService.updatePassword(id, request.getNewPassword());
//            }
//
//            if (result) {
//                return Result.ok("用户信息更新成功");
//            } else {
//                throw new ServiceException(Code.ERROR, "用户信息更新失败");
//            }
//        } catch ( BaseException e) {
//            return Result.error(e.getCode().getCode(), e.getMessage());
//        } catch (Exception e) {
//            log.error("更新用户失败", e);
//            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
//        }
//    }


//    @GetMapping("/verify")
//    public Result<Boolean> verifyToken(@RequestParam String token) {
//        try {
//            boolean isValid = userService.verify(token);
//            return Result.ok(isValid);
//        } catch (Exception e) {
//            log.error("令牌验证失败", e);
//            return Result.error(Code.ERROR.getCode(), "令牌验证失败");
//        }
//    }

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

//    private Long getCurrentUserId(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ServiceException(Code.UNAUTHORIZED, "用户未登录");
//        }
//
//        String username = null;
//
//        if (authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            username = userDetails.getUsername();
//        } else if (authentication.getPrincipal() instanceof String) {
//            username = (String) authentication.getPrincipal();
//        } else {
//            throw new ServiceException(Code.UNAUTHORIZED, "不支持的Principal类型");
//        }
//
//        if (username == null) {
//            throw new ServiceException(Code.UNAUTHORIZED, "无法解析用户名");
//        }
//
//        User user = userService.findUserByUsername(username);
//        if (user == null) {
//            throw new ServiceException(Code.NOT_FOUND, "用户不存在");
//        }
//
//        return user.getId();
//    }



//    // 添加专门的密码更新接口
//    @PutMapping("/{id}/password")
//    public Result<String> updatePassword(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdatePasswordRequest request,
//            BindingResult bindingResult) {
//
//        try {
//            // 验证请求参数
//            if (bindingResult.hasErrors()) {
//                String errorMsg = bindingResult.getFieldErrors().stream()
//                        .map(FieldError::getDefaultMessage)
//                        .collect(Collectors.joining("; "));
//                throw new ServiceException(Code.BAD_REQUEST, errorMsg);
//            }
//
//            // 验证旧密码
//            User user = userService.findUserById(id);
//            if (user == null) {
//                throw new ServiceException(Code.NOT_FOUND, "用户不存在");
//            }
//
//            if (!userService.verifyPassword(request.getOldPassword(), user.getPassword())) {
//                throw new ServiceException(Code.UNAUTHORIZED, "旧密码不正确");
//            }
//
//            // 更新密码
//            boolean result = userService.updatePassword(id, request.getNewPassword());
//
//            if (result) {
//                return Result.ok("密码更新成功");
//            } else {
//                throw new ServiceException(Code.ERROR, "密码更新失败");
//            }
//        } catch ( BaseException e) {
//            return Result.error(e.getCode().getCode(), e.getMessage());
//        } catch (Exception e) {
//            log.error("更新密码失败", e);
//            return Result.error(Code.ERROR.getCode(), "系统错误: " + e.getMessage());
//        }
//    }
//
//    @Autowired
//    Producer producer;
//    @GetMapping("/captcha")
//    public Result Captcha() throws IOException {
//        String key = UUID.randomUUID().toString();
//        String code = producer.createText();
//        BufferedImage image = producer.createImage(code);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", outputStream);
//        BASE64Encoder encoder = new BASE64Encoder();
//        String str = "data:image/jpeg;base64,";
//        String base64Img = str + encoder.encode(outputStream.toByteArray());
//        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);
//        return Result.succ(
//                MapUtil.builder()
//                        .put("userKey", key)
//                        .put("captcherImg", base64Img)
//                        .build()
//        );
//    }
//
