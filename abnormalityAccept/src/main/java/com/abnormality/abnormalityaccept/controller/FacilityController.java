package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.param.FacilityParam;
import com.abnormality.abnormalityaccept.enums.Code;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.abnormality.abnormalityaccept.entity.Facility;
import com.abnormality.abnormalityaccept.service.FacilityService;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/facility")
@Tag(name = "设施管理")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "分页查询所有设施")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/list")
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

    @Operation(summary = "多条件查询设施")
    @PostMapping("/conditions")
    public Result<PageInfo<Facility>> findFacilityByConditions(@RequestBody FacilityParam facilityParam){
        PageInfo<Facility> facilityList = facilityService.findFacilityByConditions(facilityParam);
        if(facilityList.getList() == null || facilityList.getList().isEmpty()) return Result.error(Code.NOT_FOUND.getCode(),"未查询到相关设施");
        return Result.ok(facilityList);
    }

}
