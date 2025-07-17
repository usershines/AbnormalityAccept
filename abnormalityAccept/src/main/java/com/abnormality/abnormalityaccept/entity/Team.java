package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    private Long id;
    @NotBlank(message = "小队名称不能为空")
    private String name;

    @TableField(exist = false)
    private List<User> members;

    //TODO:待更新
    private int status;

    private Long resolvingQuestId;
    @NotNull(message = "小队等级不能为空")
    @PositiveOrZero(message = "等级必须为非负数")
    private Integer level;
    private String description;
    @NotNull(message = "负责人ID不能为空")
    private Long leaderId;
    private transient List<User> members;
}
