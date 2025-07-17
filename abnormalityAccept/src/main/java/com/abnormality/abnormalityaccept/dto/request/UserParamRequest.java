package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description: 请求体封装 }
 * @since 2025-07-17
 */

@Data
public class UserParamRequest {
    private  Long id;
    private  String username;
    private  String email;
    private  Integer level;
    private  Long facilityId;
}
