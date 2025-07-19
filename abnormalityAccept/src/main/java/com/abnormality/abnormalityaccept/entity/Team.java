package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    private Long id;

    private String name;

    //@TableField(exist = false)
    private List<Long> memberIds;

    //TODO:待更新
    private int status;

    private Long resolvingQuestId;

    private Integer level;

    private String description;

    private Long leaderId;

    private Integer isActive=1;
}
