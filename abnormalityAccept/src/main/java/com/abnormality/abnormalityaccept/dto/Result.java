package com.abnormality.abnormalityaccept.dto;

import com.abnormality.abnormalityaccept.enums.Code;
import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data){
        return new Result<>(Code.SUCCESS.getCode(), "操作成功", data);
    }

    public static <T> Result<T> ok(String message, T data) {return new Result<>(Code.SUCCESS.getCode(), message, data);
    }
    public static <T> Result<T> error(int code,String msg){
        return new Result<>(code, msg, null);
    }
    public static <T> Result<T> error(String msg){
        return new Result<>(Code.ERROR.getCode(), msg, null);
    }
    public static <T> Result<T> error(){
        return new Result<>(Code.ERROR.getCode(), "操作失败", null);
    }
    public static <T> Result<T> error(T data){
        return new Result<>(Code.ERROR.getCode(), "操作失败", data);
    }
    public static <T> Result<T> error(int code,String msg,T data){
        return new Result<>(code, msg, data);
    }
}
