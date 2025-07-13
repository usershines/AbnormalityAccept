package com.abnormality.abnormalityaccept.exception;

import com.abnormality.abnormalityaccept.enums.Code;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BaseException extends RuntimeException{
    private Code code;
    private String msg;
    private List<String> msgList=new ArrayList<>();

    public BaseException(Code code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.msgList.add(msg);
    }
    public BaseException(String msg){
        super(msg);
        this.code=Code.ERROR;
        this.msg=msg;
        this.msgList.add(msg);
    }
    public BaseException(Code code, String msg,Throwable cause){
        super(msg,cause);
        this.code=code;
        this.msg=msg;
        if(cause!=null){
            if(cause instanceof BaseException e){
                this.msgList.addAll(e.getMsgList());
            }
            this.msgList.add(msg);
        }
    }

}
