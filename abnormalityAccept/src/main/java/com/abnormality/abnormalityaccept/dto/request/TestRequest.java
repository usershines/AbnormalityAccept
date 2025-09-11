package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestRequest implements Serializable {
    private String name;
    private Integer age;
}
