package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.abnormality.abnormalityaccept.entity.Facility;
import com.abnormality.abnormalityaccept.service.FacilityService;
import com.abnormality.abnormalityaccept.mapper.FacilityMapper;

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
@RequestMapping("/Facility")
@Tag(name = "设施管理")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "分页查询所有设施")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/findAll")
    public Result<PageInfo<Facility>> findAllFacility(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Facility> facilityList = facilityService.findAllFacility(pageNum, pageSize);
        return Result.ok(facilityList);
    }

    @Operation(summary = "根据ID查询设施")
    @Parameter(name = "id", description = "设施ID", required = true, example = "SITE-19")
    @GetMapping("/findById")
    public Result<Facility> findFacilityById(@RequestParam Long id) {
        Facility facility = facilityService.findFacilityById(id);
        if (facility == null) {
            return Result.error(500, "设施不存在");
        }
        return Result.ok(facility);
    }

    @Operation(summary = "添加设施")
    @PostMapping("/new")
    public Result<String> addFacility(@RequestBody Facility facility) {
        if (facilityService.addFacility(facility)) {
            return Result.ok("添加成功");
        }
        return Result.error(500, "添加失败");
    }

    @Operation(summary = "更新设施")
    @PutMapping("/update")
    public Result<String> updateFacility(@RequestBody Facility facility) {
        if (facilityService.updateFacility(facility)) {
            return Result.ok("更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @Operation(summary = "删除设施")
    @DeleteMapping("/{id}")
    public Result<String> deleteFacilityById(@PathVariable Long id) {
        if (facilityService.deleteFacilityById(id)) {
            return Result.ok("删除成功");
        }
        return Result.error(500, "删除失败");
    }
    // 拓展按等级查询设施
    @Operation(summary = "按等级查询设施信息")
    @GetMapping("/byLevel")
    public Result<List<Facility>> findByLevel(@Parameter(description = "设施等级") @RequestParam Integer level) {
        return facilityService.findByLevel(level);
    }
    // 拓展搜索设施（按名称或地址模糊查询）
    @Operation(summary = "搜索设施（按名称或地址模糊匹配）")
    @GetMapping("/search")
    public Result<List<Facility>> search(@Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return facilityService.search(keyword);
    }
}
