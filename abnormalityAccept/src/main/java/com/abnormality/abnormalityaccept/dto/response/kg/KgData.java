package com.abnormality.abnormalityaccept.dto.response.kg;

import lombok.Data;

import java.util.List;

@Data
public class KgData {

    List<KgNode> nodes;
    List<KgEdge> edges;
}


