package com.abnormality.abnormalityaccept.dto.response.kg;

import lombok.Data;

@Data
public class KgEdge{
    private String type;
    private String source;
    private String target;
    private String label;



    public static KgEdge newRelation(String origin,String relation,String target){
        KgEdge kgEdge=new KgEdge();
        kgEdge.label=relation;
        kgEdge.source=origin;
        kgEdge.target=target;
        return kgEdge;
    }


}
