package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.abnormality.abnormalityaccept.entity.Equipment;
import com.abnormality.abnormalityaccept.service.EquipmentService;
import com.abnormality.abnormalityaccept.mapper.EquipmentMapper;

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
public class EquipmentController {
    private final EquipmentService equipmentService;

    @Operation(summary = "分页查询所有装备")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/findAll")
    public Result<PageInfo<Equipment>> findAllEquipment(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Equipment> equipmentList = equipmentService.findAllEquipment(pageNum, pageSize);
        return Result.ok(equipmentList);
    }

    @Operation(summary = "根据ID查询装备")
    @Parameter(name = "id", description = "装备ID", required = true, example = "1")
    @GetMapping("/findById")
    public Result<Equipment> findEquipmentById(@RequestParam Long id) {
        Equipment equipment = equipmentService.findEquipmentById(id);
        if (equipment == null) {
            return Result.error(500, "装备不存在");
        }
        return Result.ok(equipment);
    }

    @Operation(summary = "添加装备")
    @PostMapping("/new")
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
}
