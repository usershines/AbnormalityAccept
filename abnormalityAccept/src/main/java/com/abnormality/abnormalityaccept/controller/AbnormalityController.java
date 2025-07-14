package com.abnormality.abnormalityaccept.controller;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Abnormality")
@Tag(name = "异想体管理", description = "异想体管理接口")
public class AbnormalityController {
    private final AbnormalityService abnormalityService;

    @Operation(summary = "分页查询所有异想体")
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    @GetMapping("/findAll")
    public Result<PageInfo<Abnormality>> findAllAbnormality(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageInfo<Abnormality> abnormalityList = abnormalityService.findAllAbnormality(pageNum, pageSize);
        return Result.success(abnormalityList);
    }

    @Operation(summary = "根据ID查询异想体")
    @Parameter(name = "id", description = "异想体ID", required = true, example = "SCP-173")
    @GetMapping("/findById")
    public Result<Abnormality> findAbnormalityById(@RequestParam Long id) {
        Abnormality abnormality = abnormalityService.findAbnormalityById(id);
        if (abnormality == null) {
            return Result.fail(500, "异想体不存在");
        }
        return Result.success(abnormality);
    }

    @Operation(summary = "添加异想体")
    @PostMapping("/new")
    public Result<String> addAbnormality(@RequestBody Abnormality abnormality) {
        if (abnormalityService.addAbnormality(abnormality)) {
            return Result.success("添加成功");
        }
        return Result.fail(500, "添加失败");
    }

    @Operation(summary = "更新异想体")
    @PutMapping("/update")
    public Result<String> updateAbnormality(@RequestBody Abnormality abnormality) {
        if (abnormalityService.updateAbnormality(abnormality)) {
            return Result.success("更新成功");
        }
        return Result.fail(500, "更新失败");
    }

    @Operation(summary = "删除异想体")
    @DeleteMapping("/{id}")
    public Result<String> deleteAbnormalityById(@PathVariable Long id) {
        if (abnormalityService.deleteAbnormalityById(id)) {
            return Result.success("删除成功");
        }
        return Result.fail(500, "删除失败");
    }

}
