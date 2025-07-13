package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class Team {
    private Long id;
    private String name;

    @TableField(exist = false)
    private List<User> members;

    //TODO:待更新
    private int status;

    private Long resolvingQuestId;
    private Integer level;
    private String description;
    private Long leaderId;
}
