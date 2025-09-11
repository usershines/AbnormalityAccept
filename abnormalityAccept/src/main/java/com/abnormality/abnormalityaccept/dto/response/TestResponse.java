package com.abnormality.abnormalityaccept.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestResponse implements Serializable {
    private String name;
    private Integer age;
    private String msg;
}
