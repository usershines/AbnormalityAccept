package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.AuthRequest;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody User user){
        if(userService.addUser(user)){
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

    //TODO:Email字段设计
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody AuthRequest req) {
        return Result.ok(userService.login(req.getName(), req.getPassword()));
    }
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody AuthRequest req) {
        return Result.ok(userService.register(req.getName(), req.getPassword(), req.getEmail()));
    }

}