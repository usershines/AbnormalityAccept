//package com.abnormality.abnormalityaccept.controller;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-13
// */
//@RestController
//@CrossOrigin
//@Tag(name = "小队管理")
//public class TeamController {
//
//
//}
package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.TeamUpdateRequest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.TeamParam;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.service.TeamService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/Team")
@Tag(name = "机动小队管理", description = "包含小队创建、更新、任务指派等接口")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "查询所有小队")
    @PostMapping("/list")
    @Level(allowLevel = {5})
    public Result<PageInfo<Team>> findAllTeam(@Parameter Integer pageNum, @RequestParam Integer pageSize){
        PageInfo<Team> teamList = teamService.findAllTeam(pageNum, pageSize);
        return Result.ok(teamList);
    }
    @Operation(summary = "根据id查询小队")
    @PostMapping("/{id}")
    @Level(allowLevel = {5})
    public Result<Team> findTeamById(@PathVariable Long id){
        Team team = teamService.findTeamById(id);
        return Result.ok(team);
    }

    @Operation(summary = "查询暂无机动小队归属的用户列表")
    @PostMapping("/usersBelongNotTeam")
    @Level(allowLevel = {5})
    public Result<PageInfo<User>> findUserBelongNotTeam(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo<User> userList = teamService.findUserBelongNotTeam(pageNum, pageSize);
        return Result.ok(userList);
    }

    @Operation(summary = "创建小队")
    @PostMapping("/add")
    @Level(allowLevel = {5})
    public Result<String> createTeam(@RequestBody Team team, @RequestParam Long leaderId){
        if(teamService.createTeam(team, leaderId)) return Result.ok("创建成功");
        return Result.error("创建失败");
    }

    @Operation(summary = "更新小队信息")
    @PutMapping("/update")
    @Level(allowLevel = {5})
    public Result<String> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest){
        if(teamService.updateTeam(teamUpdateRequest)) return Result.ok("更新成功");
        return Result.error("更新失败");
    }

    @Operation(summary = "添加小队成员")
    @PostMapping("/addMember")
    @Level(allowLevel = {5})
    public Result<String> addMemberToTeam(@RequestParam Long teamId, @RequestParam Long userId){
        if(teamService.addMemberToTeam(teamId, userId)) return Result.ok("添加成功");
        return Result.error("添加失败");
    }

    @Operation(summary = "移除小队成员")
    @PostMapping("/removeMember")
    @Level(allowLevel = {5})
    public Result<String> removeMemberFromTeam(@RequestParam Long teamId, @RequestParam Long userId){
        if(teamService.removeMemberFromTeam(teamId, userId)) return Result.ok("移除成功");
        return Result.error("移除失败");
    }

    @Operation(summary = "删除小队")
    @DeleteMapping("/delete")
    @Level(allowLevel = {5})
    public Result<String> deleteTeamById(@RequestParam Long id){
        if(teamService.deleteTeamById(id)) return Result.ok("删除成功");
        return Result.error("删除失败");
    }
    @Operation(summary = "分页多条件查询")
    @GetMapping("/conditions")
    @Level(allowLevel = {5})
    public Result<PageInfo<Team>> findTeamByConditions(TeamParam teamParam){
        PageInfo<Team> teamList = teamService.findTeamByConditions(teamParam);
        if(teamList.getList() ==null || teamList.getList().isEmpty() ) return Result.error(Code.ERROR.getCode(),"未查询到相关小队");
        return Result.ok(teamList);
    }

}
