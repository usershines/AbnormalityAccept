package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.param.AbnormalityParam;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.service.AbnormalityService;
import com.abnormality.abnormalityaccept.service.FileService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@RequestMapping("/abnormality")
@Tag(name = "异想体管理")
public class AbnormalityController {

    @Autowired
    private final AbnormalityService abnormalityService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Operation(summary = "异想体信息查询")
    @GetMapping("/list")
    public Result<PageInfo<Abnormality>> findAllAbnormality(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<Abnormality> abnormalityList = abnormalityService.findAllAbnormality(pageNum, pageSize);
        List<Abnormality> abnormalityList1 = fileService.completeImageUrl(abnormalityList.getList());
        return Result.ok(PageInfo.of(abnormalityList1));
    }
    @Operation(summary = "根据id查询异想体")
    @GetMapping("/{id}")
    public Result<Abnormality> findAbnormalityById(@PathVariable Long id) {
        Abnormality abnormality = completeImageUrl(abnormalityService.findAbnormalityById(id));
        return Result.ok(abnormality);
    }
    @Operation(summary = "添加异想体")
    @PostMapping("/add")
    public Result<String> addAbnormality(@RequestBody Abnormality abnormality) {
        if (abnormalityService.addAbnormality(abnormality)) {
            return Result.ok("添加成功");
        } else {
            return Result.error(Code.ERROR.getCode(),"添加失败");
        }
    }
    @Operation(summary = "更新异想体")
    @PutMapping("/update")
    public Result<String> updateAbnormality(@RequestBody Abnormality abnormality) {
        Long editorId = userService.getUserIdByToken();
        if (abnormalityService.updateAbnormality(abnormality, editorId)) {
            return Result.ok("更新成功");
        } else {
            return Result.error(Code.ERROR.getCode(),"更新失败");
        }
    }
    @Operation(summary = "分页条件查询异想体信息")
    @GetMapping("/conditions")
    public Result<PageInfo<Abnormality>> findAbnormalityByConditions(AbnormalityParam abnormalityParam){

        PageInfo<Abnormality> abnormalityList = abnormalityService.findAbnormalityByConditions(abnormalityParam);
        List<Abnormality> abnormalityList1 = fileService.completeImageUrl(abnormalityList.getList());
        if (abnormalityList.getList()==null || abnormalityList.getList().isEmpty())
            return Result.error(Code.NOT_FOUND.getCode(), "未查询到符合条件的异想体数据");
        return Result.ok(PageInfo.of(abnormalityList1));

    }


    private Abnormality completeImageUrl(Abnormality abnormality) {
        abnormality.setImgName(fileService.getPublicUrl(abnormality.getImgName()));
        return abnormality;
    }

    private List<Abnormality> completeImageUrl(List<Abnormality> abnormalityList) {
        List<Abnormality> abnormalityVoList = new ArrayList<>();
        for(Abnormality abnormality:abnormalityList){
            abnormalityVoList.add(completeImageUrl(abnormality));
        }
        return abnormalityVoList;
    }

}
