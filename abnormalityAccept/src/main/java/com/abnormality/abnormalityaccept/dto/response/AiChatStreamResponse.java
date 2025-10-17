package com.abnormality.abnormalityaccept.dto.response;

import com.abnormality.abnormalityaccept.dto.response.kg.KgData;
import com.abnormality.abnormalityaccept.service.impl.AiChatServiceImplLocal;
import lombok.Data;

@Data
public class AiChatStreamResponse {
    private String type;
    private Object data;


    public static AiChatStreamResponse newText(String text) {
        AiChatStreamResponse response = new AiChatStreamResponse();
        response.setType("text");
        response.setData(text);
        return response;
    }

    public static AiChatStreamResponse newGraph(KgData kgData){
        AiChatStreamResponse response = new AiChatStreamResponse();
        response.setType("graph");
        response.setData(kgData);
        return response;
    }
}



