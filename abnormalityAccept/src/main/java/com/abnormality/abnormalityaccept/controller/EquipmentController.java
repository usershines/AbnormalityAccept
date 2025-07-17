package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.github.pagehelper.PageHelper;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.abnormality.abnormalityaccept.service.EquipmentService;
import com.abnormality.abnormalityaccept.mapper.EquipmentMapper;


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
@RequestMapping("/Equipment")
@Tag(name = "装备管理")
public class EquipmentController {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
    private final EquipmentService equipmentService;

    @Operation(summary = "分页查询所有装备")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/findAllEquipment")
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
            return Result.error(500, "装备不存在");
        }
        return Result.ok(equipment);
    }

    @Operation(summary = "添加装备")
    @PostMapping("/add")
    public Result<String> addEquipment(@RequestBody Equipment equipment) {
        if (equipmentService.addEquipment(equipment)) {
            return Result.ok("添加成功");
        }
        return Result.error(500, "添加失败");
    }

    @Operation(summary = "更新装备")
    @PutMapping("/update")
    public Result<String> updateEquipment(@RequestBody Equipment equipment) {
        if (equipmentService.updateEquipment(equipment)) {
            return Result.ok("更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @Operation(summary = "删除装备")
    @DeleteMapping("/{id}")
    public Result<String> deleteEquipmentById(@PathVariable Long id) {
        if (equipmentService.deleteEquipmentById(id)) {
            return Result.ok("删除成功");
        }
        return Result.error(500, "删除失败");
    }

    // 拓展接口
    @Operation(summary = "根据装备状态查询装备")
    @GetMapping("/state/{state}")
    public Result<List<Equipment>> findByState(@PathVariable String state) {
        List<Equipment> data = equipmentService.findByState(state);
        return Result.ok(data);
    }

    @Operation(summary = "批量更新装备")
    @PutMapping("/batch/state")
    public Result<String> batchUpdateState(
            @RequestParam List<Long> ids,
            @RequestParam String state) {
        boolean success = equipmentService.batchUpdateState(ids, state);
        return success ? Result.ok("批量更新成功") : Result.error(500, "批量更新失败");
    }
    @Operation(summary = "批量删除装备")
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        boolean success = equipmentService.batchDelete(ids);
        return success ? Result.ok("批量删除成功") : Result.error(500, "批量删除失败");
    }
    @Operation(summary = "模糊查询（name）")
    @GetMapping("/search/name")
    public Result<List<Equipment>> searchByName(@RequestParam String name) {
        List<Equipment> list = equipmentService.findByName(name);
        return Result.ok(list);
    }

}
