package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.abnormality.abnormalityaccept.entity.Notice;
import com.abnormality.abnormalityaccept.service.NoticeService;
import com.abnormality.abnormalityaccept.mapper.NoticeMapper;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/notice")
@Tag(name = "公告管理")
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "分页查询所有通知")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/list")
    public Result<PageInfo<Notice>> findAllNotice(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Notice> noticeList = noticeService.findAllNotice(pageNum, pageSize);
        return Result.ok(noticeList);
    }

    @Operation(summary = "根据ID查询通知")
    @Parameter(name = "id", description = "通知ID", required = true, example = "1")
    @GetMapping("/findById")
    public Result<Notice> findNoticeById(@RequestParam Long id) {
        Notice notice = noticeService.findNoticeById(id);
        if (notice == null) {
            return Result.error(500, "通知不存在");
        }
        return Result.ok(notice);
    }

    @Operation(summary = "添加通知")
    @PostMapping("/new")
    public Result<String> addNotice(@RequestBody Notice notice) {
        if (noticeService.addNotice(notice)) {
            return Result.ok("添加成功");
        }
        return Result.error(500, "添加失败");
    }

    @Operation(summary = "更新通知")
    @PutMapping("/update")
    public Result<String> updateNotice(@RequestBody Notice notice) {
        if (noticeService.updateNotice(notice)) {
            return Result.ok("更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<String> deleteNoticeById(@PathVariable Long id) {
        if (noticeService.deleteNoticeById(id)) {
            return Result.ok("删除成功");
        }
        return Result.error(500, "删除失败");
    }

}
