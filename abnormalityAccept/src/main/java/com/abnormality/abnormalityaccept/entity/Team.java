package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    //@TableField(exist = false)
    private List<Long> memberIds;

    //TODO:待更新
    private int status;

    private Long resolvingQuestId;
    private String resolvingQuestName;

    private Integer level;

    private String description;

    private Long leaderId;

    private Integer isActive=1;
}
