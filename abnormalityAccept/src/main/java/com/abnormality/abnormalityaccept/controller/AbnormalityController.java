package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.synonyms.GetSynonymsSetsAction;
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
@RequestMapping("/abnormality")
@Tag(name = "异想体管理")
public class AbnormalityController {
    private final AbnormalityService abnormalityService;

    @Operation(summary = "异想体信息查询")
    @RequestMapping("/List")
    public Result<PageInfo<Abnormality>> findAllAbnormality(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<Abnormality> abnormalityList = abnormalityService.findAllAbnormality(pageNum, pageSize);
        return Result.ok(abnormalityList);
    }
    @Operation(summary = "根据id查询异想体")
    @RequestMapping("/{id}")
    public Result<Abnormality> findAbnormalityById(@PathVariable Long id) {
        Abnormality abnormality = abnormalityService.findAbnormalityById(id);
        if (abnormality == null) {
            return Result.error(500,"异想体不存在");
        }
        return Result.ok(abnormality);
    }
    @Operation(summary = "添加异想体")
    @RequestMapping("/add")
    public Result<String> addAbnormality(@RequestBody Abnormality abnormality) {
        if (abnormalityService.addAbnormality(abnormality)) {
            return Result.ok("添加成功");
        } else {
            return Result.error(500,"添加失败");
        }
    }
    @Operation(summary = "更新异想体")
    @RequestMapping("/update")
    public Result<String> updateAbnormality(@RequestBody Abnormality abnormality) {
        if (abnormalityService.updateAbnormality(abnormality)) {
            return Result.ok("更新成功");
        } else {
            return Result.error(500,"更新失败");
        }
    }
    @Operation(summary = "分页条件查询异想体信息")
    @RequestMapping("/conditions")
    public Result<PageInfo<Abnormality>> findAbnormalityByConditions(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Long facilityId,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize)
    {
        Abnormality abnormality = new Abnormality();
        abnormality.setId(id);
        abnormality.setName(name);
        abnormality.setLevel(level);
        abnormality.setFacilityId(facilityId);
        PageInfo<Abnormality> abnormalityList = abnormalityService.findAbnormalityByConditions(abnormality, pageNum, pageSize);
        return Result.ok(abnormalityList);

    }


}
