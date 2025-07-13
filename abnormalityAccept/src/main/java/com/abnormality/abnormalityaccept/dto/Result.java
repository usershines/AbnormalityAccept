package com.abnormality.abnormalityaccept.dto;

import lombok.Data;

@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data){
        return new Result<T>("200","操作成功",data);
    }
    public static <T> Result<T> error(String code,String msg){
        return new Result<T>(code,msg,null);
    }
    public static <T> Result<T> error(String msg){
        return new Result<T>("500",msg,null);
    }
    public static <T> Result<T> error(){
        return new Result<T>("500","操作失败",null);
    }
}
