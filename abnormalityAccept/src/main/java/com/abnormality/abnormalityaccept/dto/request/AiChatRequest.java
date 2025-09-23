package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

@Data
public class AiChatRequest {

    private String message;
    private String userId;
    private String userName;
    private boolean contentOnly;

}
