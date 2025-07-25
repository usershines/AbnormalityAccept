package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.QuestRequest;
import com.abnormality.abnormalityaccept.entity.Notice;
import com.abnormality.abnormalityaccept.entity.Quest;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.param.QuestParam;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.service.NoticeService;
import com.abnormality.abnormalityaccept.service.QuestService;
import com.abnormality.abnormalityaccept.service.TeamService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/quest")
@Tag(name = "任务管理")
public class QuestController {
    private final QuestService questService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Operation(summary = "分页查询所有任务")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/list")
    public Result<PageInfo<Quest>> findAllQuest(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Quest> questList = questService.findAllQuest(pageNum, pageSize);
        return Result.ok(questList);
    }

    @Operation(summary = "根据ID查询任务")
    @Parameter(name = "id", description = "任务ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<Quest> findNoticeById(@PathVariable Long id) {
        Quest quest = questService.findQuestById(id);
        if (quest == null) {
            return Result.error(Code.NOT_FOUND.getCode(), "未查询到相关任务");
        }
        return Result.ok(quest);
    }

    @Operation(summary = "添加任务")
    @PostMapping("/new")
    public Result<String> addQuest(@RequestBody QuestRequest  questRequest) {
        Long sendId = userService.getUserIdByToken();
        if (questService.addQuest(questRequest,sendId)) {
            return Result.ok("添加成功");
        }
        return Result.error(Code.ERROR.getCode(), "添加失败");
    }

    @Operation(summary = "更新任务")
    @PutMapping("/update")
    public Result<String> updateQuest(@RequestBody Quest quest) {
        if (questService.updateQuest(quest)) {
            return Result.ok("更新成功");
        }
        return Result.error(Code.ERROR.getCode(), "更新失败");
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public Result<String> deleteQuestById(@PathVariable Long id) {
        if (questService.deleteQuestById(id)) {
            return Result.ok("删除成功");
        }
        return Result.error(Code.ERROR.getCode(), "删除失败");
    }

    @Operation(summary = "条件分页查询任务")
    @PostMapping("/conditions")
    public Result<PageInfo<Quest>> findQuestByCondition  (@RequestBody QuestParam questParam) {
        PageInfo<Quest> questList = questService.findQuestByConditions(questParam);
        if(questList.getList()== null || questList.getList().isEmpty()) return Result.error(Code.NOT_FOUND.getCode(),"未查询到相关任务信息");
        return Result.ok(questList);
    }

    @Operation(summary = "查询空闲状态的小队")
    @GetMapping("/teamLeisure")
    public Result<PageInfo<Team>> findTeamLeisure(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<Team> teamList = teamService.findTeamLeisure(pageNum, pageSize);
        return Result.ok(teamList);
    }
}
