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

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "机动小队管理", description = "包含小队创建、更新、任务指派等接口")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 创建机动小队（仅O5权限）
     */
    @Operation(summary = "创建机动小队", description = "需要O5权限，创建新的特遣小队")
    @PostMapping
    public Result<Team> createTeam(
            @Valid @RequestBody Team team,
            @Parameter(description = "创建者信息（从token中解析）") @RequestAttribute("currentUser") User creator
    ) {
        Team createdTeam = teamService.createTeam(team, creator);
        return Result.ok(createdTeam, "小队创建成功：" + createdTeam.getName());
    }

    /**
     * 更新小队信息
     */
    @Operation(summary = "更新小队信息", description = "修改小队名称、描述、等级、负责人等基础信息")
    @PutMapping("/{teamId}")
    public Result<Team> updateTeam(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId,
            @Valid @RequestBody Team updatedData
    ) {
        Team updatedTeam = teamService.updateTeam(teamId, updatedData);
        return Result.ok(updatedTeam, "小队更新成功");
    }

    /**
     * 指派任务给小队
     */
    @Operation(summary = "指派任务给小队", description = "仅待命状态的小队可接收任务")
    @PatchMapping("/{teamId}/quest")
    public Result<Void> assignQuest(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "任务ID", required = true) @RequestParam Long questId
    ) {
        teamService.assignQuestToTeam(teamId, questId);
        return Result.ok(null, "任务指派成功");
    }

    /**
     * 更新小队状态
     */
    @Operation(summary = "更新小队状态", description = "状态可选值：待命/执行任务/休整")
    @PatchMapping("/{teamId}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "新状态", required = true) @RequestParam String newStatus
    ) {
        teamService.updateTeamStatus(teamId, newStatus);
        return Result.ok(null, "状态更新为：" + newStatus);
    }

    /**
     * 添加成员到小队
     */
    @Operation(summary = "添加成员到小队")
    @PostMapping("/{teamId}/members")
    public Result<Void> addMember(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId
    ) {
        teamService.addMemberToTeam(teamId, userId);
        return Result.ok(null, "成员添加成功");
    }

    /**
     * 移除小队成员
     */
    @Operation(summary = "移除小队成员")
    @DeleteMapping("/{teamId}/members/{userId}")
    public Result<Void> removeMember(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId
    ) {
        teamService.removeMemberFromTeam(teamId, userId);
        return Result.ok(null, "成员移除成功");
    }

    /**
     * 获取小队详情
     */
    @Operation(summary = "获取小队详情", description = "包含成员列表、当前任务等信息")
    @GetMapping("/{teamId}")
    public Result<Team> getTeamDetails(
            @Parameter(description = "小队ID", required = true) @PathVariable Long teamId
    ) {
        Team team = teamService.getTeamDetails(teamId);
        return Result.ok(team);
    }
}
