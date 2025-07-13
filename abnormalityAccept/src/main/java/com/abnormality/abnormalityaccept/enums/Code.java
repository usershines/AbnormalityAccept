package com.abnormality.abnormalityaccept.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Code {

    SUCCESS("200", "成功"),
    ERROR("500", "服务器错误"),
    NOT_FOUND("404", "未找到"),
    NOT_ALLOWED("405", "不允许"),
    NOT_AUTHENTICATED("401", "未认证"),
    NOT_AUTHORIZED("403", "未授权"),
    NOT_SUPPORTED("415", "不支持"),
    NOT_ACCEPTABLE("406", "不可接受"),
    NOT_ACCEPTABLE_CONTENT("406", "不可接受");
    private final String code;
    private final String msg;

}
