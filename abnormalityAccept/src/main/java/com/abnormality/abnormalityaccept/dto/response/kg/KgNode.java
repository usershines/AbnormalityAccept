package com.abnormality.abnormalityaccept.dto.response.kg;

import lombok.Data;

@Data
public class KgNode{
    private String id;
    private String label;
    private String type;

    public static KgNode newNode(String name){
        KgNode kgNode=new KgNode();
        kgNode.id=name;
        kgNode.label=name;
        return kgNode;
    }
}
