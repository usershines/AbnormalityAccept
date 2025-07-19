package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.param.EquipmentParam;
import com.abnormality.abnormalityaccept.enums.Code;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.abnormality.abnormalityaccept.service.EquipmentService;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/equipment")
@Tag(name = "装备管理")
public class EquipmentController {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
    private final EquipmentService equipmentService;

    @Operation(summary = "分页查询所有装备")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/list")
    public Result<PageInfo<Equipment>> findAllEquipment(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Equipment> equipmentList = equipmentService.findAllEquipment(pageNum, pageSize);
        logger.info("PageInfo: {}", equipmentList);
        return Result.ok(equipmentList);
    }

    @Operation(summary = "根据ID查询装备")
    @Parameter(name = "id", description = "装备ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<Equipment> findEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.findEquipmentById(id);
        if (equipment == null) {
            return Result.error(Code.NOT_FOUND.getCode(), "未查询到该装备");
        }
        return Result.ok(equipment);
    }

    @Operation(summary = "添加装备")
    @PostMapping("/add")
    public Result<String> addEquipment(@RequestBody Equipment equipment) {
        if (equipmentService.addEquipment(equipment)) {
            return Result.ok("添加成功");
        }
        return Result.error(Code.ERROR.getCode(), "添加失败");
    }

    @Operation(summary = "更新装备")
    @PutMapping("/update")
    public Result<String> updateEquipment(@RequestBody Equipment equipment) {
        if (equipmentService.updateEquipment(equipment)) {
            return Result.ok("更新成功");
        }
        return Result.error(Code.ERROR.getCode(), "更新失败");
    }

    @Operation(summary = "删除装备")
    @DeleteMapping("/{id}")
    public Result<String> deleteEquipmentById(@PathVariable Long id) {
        if (equipmentService.deleteEquipmentById(id)) {
            return Result.ok("删除成功");
        }
        return Result.error(Code.ERROR.getCode(), "删除失败");
    }

    // 拓展接口
    @Operation(summary = "批量更新装备")
    @PutMapping("/batch/state")
    public Result<String> batchUpdateState(
            @RequestParam List<Long> ids,
            @RequestParam String state) {
        boolean success = equipmentService.batchUpdateState(ids, state);
        return success ? Result.ok("批量更新成功") : Result.error(Code.ERROR.getCode(), "批量更新失败");
    }
    @Operation(summary = "批量删除装备")
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        boolean success = equipmentService.batchDelete(ids);
        return success ? Result.ok("批量删除成功") : Result.error(Code.ERROR.getCode(), "批量删除失败");
    }

    @Operation(summary = "分页多条件查询")
    @GetMapping("/conditions")
    public Result<PageInfo<Equipment>> findEquipmentByConditions(EquipmentParam equipmentParam) {
        PageInfo<Equipment> equipmentList = equipmentService.findEquipmentByConditions(equipmentParam);
        if(equipmentList == null || equipmentList.getList().isEmpty()) {
            return Result.error(Code.NOT_FOUND.getCode(), "未查询到相关装备");
        }

        return Result.ok(equipmentList);
    }

}
